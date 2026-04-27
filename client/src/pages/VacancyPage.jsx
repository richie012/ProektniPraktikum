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
    const [existingApplication, setExistingApplication] = useState(null);
    const [checkingApplication, setCheckingApplication] = useState(false);

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

    useEffect(() => {
        if (!canApply) {
            setExistingApplication(null);
            return;
        }

        setCheckingApplication(true);
        API.get("/applications", { params: { studentId: user.studentId } })
            .then((res) => {
                const applications = Array.isArray(res.data) ? res.data : [];
                const found = applications.find((app) => Number(app.vacancyId) === Number(id));
                setExistingApplication(found || null);
            })
            .catch((err) => {
                console.error(err);
                setExistingApplication(null);
            })
            .finally(() => setCheckingApplication(false));
    }, [id, canApply, user?.studentId]);

    const handleApply = async () => {
        try {
            const response = await API.post("/applications", {
                studentId: user.studentId,
                vacancyId: id,
                coverLetter: "Хочу эту стажировку"
            });
            setApplyStatus("success");
            setExistingApplication({
                id: response.data?.id,
                vacancyId: Number(id),
                status: response.data?.status || "PENDING",
                createdAt: response.data?.createdAt || null,
            });
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

                <Card.Body className="p-4 p-md-4 p-sm-2">
                    {/* Форма отклика теперь сразу под заголовком */}
                    <div className="mb-4">
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
                        {canApply && checkingApplication && (
                            <div className="d-flex align-items-center gap-2 text-muted">
                                <Spinner animation="border" size="sm" />
                                <span>Проверяем вашу заявку...</span>
                            </div>
                        )}
                        {canApply && !checkingApplication && existingApplication && (
                            <Alert variant="info" className="mb-0 d-flex justify-content-between align-items-center">
                                <span>ℹ️ Вы уже откликнулись на эту вакансию.</span>
                                <Button as={Link} to="/profile?tab=applications" variant="outline-primary" size="sm">
                                    Перейти к заявке
                                </Button>
                            </Alert>
                        )}
                        {canApply && !checkingApplication && !existingApplication && (
                            <Button variant="success" size="lg" className="w-100 mt-2 mb-2" onClick={handleApply}>
                                Откликнуться на стажировку
                            </Button>
                        )}
                        {!canApply && (
                            <Alert variant="info" className="mb-0">
                                ℹ️ {user
                                    ? "Отклик доступен только для студентов с заполненным профилем"
                                    : <>Чтобы откликнуться, <Link to="/login">войдите в систему</Link></>
                                }
                            </Alert>
                        )}
                    </div>

                    <h6 className="text-muted text-uppercase mb-2" style={{ letterSpacing: "0.05em" }}>
                        Описание вакансии
                    </h6>
                    <p className="text-dark lh-lg">{vacancy.description}</p>
                </Card.Body>
            </Card>
        </Container>
    );
}