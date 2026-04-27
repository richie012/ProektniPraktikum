import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import API from "../api/api";
import {useAuth} from "../context/AuthContext";

export default function VacancyPage() {
    const {id} = useParams();
    const {user} = useAuth();
    const canApply = Boolean(user && user.role === "STUDENT" && user.studentId);

    const [vacancy, setVacancy] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        API.get(`/vacancies/${id}`)
            .then(res => {
                setVacancy(res.data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setLoading(false);
            });
    }, [id]);

    const handleApply = async () => {
        try {
            await API.post("/applications", {
                studentId: user.studentId,
                vacancyId: id,
                coverLetter: "Хочу эту стажировку"
            });

            alert("Отклик отправлен");
        } catch (e) {
            alert("Ошибка при отклике");
        }
    };

    if (loading) return <p>Загрузка...</p>;
    if (!vacancy) return <p>Вакансия не найдена</p>;

    return (
        <div style={{padding: "20px"}}>
            <h1>{vacancy.title}</h1>

            <p>
                <b>Компания:</b> {vacancy.companyName}
            </p>

            <p>{vacancy.description}</p>

            <hr/>

            {canApply ? (
                <button onClick={handleApply}>
                    Откликнуться
                </button>
            ) : (
                <p style={{color: "gray"}}>
                    {user ? "Отклик доступен только студентам" : "Войдите, чтобы откликнуться"}
                </p>
            )}
        </div>
    );
}