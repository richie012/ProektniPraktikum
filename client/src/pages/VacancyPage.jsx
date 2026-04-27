import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import API from "../api/api";

export default function VacancyPage() {
    const { id } = useParams();
    const [vacancy, setVacancy] = useState(null);

    useEffect(() => {
        API.get(`/vacancies/${id}`)
            .then(res => setVacancy(res.data))
            .catch(err => console.error(err));
    }, [id]);

    const handleApply = () => {
        API.post("/applications", {
            studentId: 1,
            vacancyId: id,
            coverLetter: "Хочу эту стажировку"
        })
            .then(() => alert("Отклик отправлен"))
            .catch(() => alert("Ошибка"));
    };

    if (!vacancy) return <p>Загрузка...</p>;

    return (
        <div>
            <h1>{vacancy.title}</h1>
            <p>{vacancy.description}</p>

            <button onClick={handleApply}>
                Откликнуться
            </button>
        </div>
    );
}