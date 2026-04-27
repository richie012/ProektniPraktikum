import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import API from "../api/api";
import { useAuth } from "../context/AuthContext";
import { Container, Card, Badge, Button, Spinner, Alert } from "react-bootstrap";

export default function VacancyPage() {
    const { id } = useParams();
    const { user } = useAuth();
    const canApply = Boolean(user && user.role === "STUDENT" && user.studentId);

    const [vacancy, setVacancy] = useState(null);
    const [loading, setLoading] = useState(true);
    const [applyStatus, setApplyStatus] = useState(null); // "success" | "error"

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
            setApplyStatus("success");
        } catch (e) {
            setApplyStatus("error");
        }
    };

    if (loading) return (
        <Container className="mt-5 d-flex justify-content-center">
            <Spinner animation="border" variant="primary" role="status">
                <span className="visually-hidden">Загрузка...</span>
            </Spinner>
        </Container>
    );

    if (!vacancy) return (
        <Container className="mt-5">
            <Alert variant="warning">⚠️ Вакансия не найдена.</Alert>
        </Container>
    );

    return (
        <Container className="py-4" style={{ maxWidth: "740px" }}>
            <Link to="/" className="btn btn-outline-secondary btn-sm mb-3">
                ← Назад к вакансиям
            </Link>

            <Card className="shadow border-0">
                <Card.Header className="bg-primary text-white py-3">
                    <h4 className="mb-1">{vacancy.title}</h4>
                    <Badge bg="light" text="dark" className="fs-6">
                        🏢 {vacancy.companyName}
                    </Badge>
                </Card.Header>

                <Card.Body className="p-4">
                    <h6 className="text-muted text-uppercase mb-2" style={{ letterSpacing: "0.05em" }}>
                        Описание вакансии
                    </h6>
                    <p className="text-dark lh-lg">{vacancy.description}</p>

                    <hr className="my-3" />

                    {applyStatus === "success" && (
                        <Alert variant="success" className="mb-3">
                            ✅ Отклик успешно отправлен! Ожидайте ответа работодателя.
                        </Alert>
                    )}
                    {applyStatus === "error" && (
                        <Alert variant="danger" className="mb-3">
                            ❌ Ошибка при отправке отклика. Попробуйте ещё раз.
                        </Alert>
                    )}

                    {canApply ? (
                        <Button
                            variant="primary"
                            onClick={handleApply}
                            disabled={applyStatus === "success"}
                        >
                            {applyStatus === "success" ? "Отклик отправлен" : "Откликнуться на стажировку"}
                        </Button>
                    ) : (
                        <Alert variant="info" className="mb-0">
                            ℹ️ {user
                                ? "Отклик доступен только для студентов с заполненным профилем"
                                : <>Чтобы откликнуться, <Link to="/login">войдите в систему</Link></>
                            }
                        </Alert>
                    )}
                </Card.Body>
            </Card>
        </Container>
    );
}