import { Link } from "react-router-dom";

export default function VacancyCard({ vacancy }) {
    return (
        <div style={{ border: "1px solid gray", margin: "10px", padding: "10px" }}>
            <h3>{vacancy.title}</h3>
            <p>{vacancy.companyName}</p>

            <Link to={`/vacancy/${vacancy.id}`}>
                Подробнее
            </Link>
        </div>
    );
}