import {useEffect, useState} from "react";
import API from "../api/api";
import VacancyCard from "../components/VacancyCard";

export default function VacanciesPage() {
    const [vacancies, setVacancies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        API.get("/vacancies")
            .then(res => {
                setVacancies(Array.isArray(res.data) ? res.data : []);
                setError("");
            })
            .catch(err => {
                console.error(err);
                setError("Не удалось загрузить вакансии. Проверьте, что сервер запущен.");
            })
            .finally(() => setLoading(false));
    }, []);

    return (
        <div>
            <h1>Вакансии</h1>
            {loading && <p>Загрузка вакансий...</p>}
            {!loading && error && <p>{error}</p>}
            {!loading && !error && vacancies.length === 0 && <p>Вакансий пока нет.</p>}
            {vacancies.map(v => (
                <VacancyCard key={v.id} vacancy={v}/>
            ))}
        </div>
    );
}