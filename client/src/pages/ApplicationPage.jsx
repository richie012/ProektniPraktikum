import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import API from "../api/api";
import { Container, Card, Badge, Alert, Spinner, Button, Form } from "react-bootstrap";

const STATUS_META = {
    PENDING: { label: "На рассмотрении", bg: "warning", text: "dark" },
    ACCEPTED: { label: "Принята", bg: "success", text: "white" },
    REJECTED: { label: "Отклонена", bg: "danger", text: "white" },
    CLOSED: { label: "Закрыта", bg: "secondary", text: "white" },
};

function formatDate(iso) {
    if (!iso) return "-";
    return new Date(iso).toLocaleString("ru-RU", {
        day: "2-digit", month: "2-digit", year: "numeric",
        hour: "2-digit", minute: "2-digit",
    });
}

export default function ApplicationPage({ user }) {
    const { id } = useParams();
    const navigate = useNavigate();
    const [app, setApp] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [statusLoading, setStatusLoading] = useState(false);
    const [statusError, setStatusError] = useState("");
    const [reviewText, setReviewText] = useState("");
    const [reviewLoading, setReviewLoading] = useState(false);
    const [reviewError, setReviewError] = useState("");
    const [reviewSuccess, setReviewSuccess] = useState(false);
    const [reviewRating, setReviewRating] = useState(null);

    useEffect(() => {
        setLoading(true);
        setError("");
        API.get(`/applications/${id}`)
            .then(res => {
                console.log("API response:", res);
                if (typeof res.data === "string") {
                    setError("Ошибка: сервер вернул невалидные данные (HTML вместо JSON). Проверьте backend.");
                    setApp(null);
                } else if (!res.data || typeof res.data !== "object" || !res.data.id) {
                    setError("Отклик не найден или данные некорректны.");
                    setApp(null);
                } else {
                    setApp(res.data);
                }
            })
            .catch((e) => {
                if (e?.response?.status === 401) {
                    setError("401: Не авторизовано. Пожалуйста, войдите в систему повторно.");
                } else {
                    setError("Не удалось загрузить отклик.");
                }
                console.log("API error:", e);
            })
            .finally(() => setLoading(false));
    }, [id]);

    const handleStatus = async (status) => {
        setStatusError("");
        setStatusLoading(true);
        try {
            const res = await API.patch(`/applications/${id}/status`, { status });
            setApp(prev => ({ ...prev, status: res.data.status }));
        } catch (err) {
            setStatusError("Не удалось изменить статус. Попробуйте позже.");
        } finally {
            setStatusLoading(false);
        }
    };

    const handleReview = async () => {
        setReviewError("");
        setReviewSuccess(false);
        setReviewLoading(true);
        try {
            const comment = reviewText.trim();
            const rating = Number(reviewRating);
            if (!comment) {
                setReviewError("Отзыв не может быть пустым.");
                setReviewLoading(false);
                return;
            }
            if (!rating || rating < 1 || rating > 100) {
                setReviewError("Оценка должна быть от 1 до 100.");
                setReviewLoading(false);
                return;
            }
            const payload = { applicationId: Number(id), comment, rating };
            const res = await API.post(`/review`, payload);
            if (res.status === 200 || res.status === 201) {
                setApp(prev => ({ ...prev, review: { ...prev.review, comment: res.data.comment, rating: res.data.rating } }));
                setReviewText("");
                setReviewRating(5);
                setReviewSuccess(true);
            } else {
                setReviewError("Не удалось отправить отзыв. Попробуйте позже.");
            }
        } catch (e) {
            if (e?.response?.data?.message) {
                setReviewError(e.response.data.message);
            } else {
                setReviewError("Не удалось отправить отзыв. Попробуйте позже.");
            }
        } finally {
            setReviewLoading(false);
        }
    };

    if (loading) return <Container className="mt-5"><Spinner animation="border" /> Загрузка...<div className="mt-3 small">id: {id}</div></Container>;
    if (error) return (
        <Container className="mt-5">
            <Alert variant="danger">{error}</Alert>
            {error.startsWith("401") && (
                <div className="mb-3">
                    <Button variant="primary" onClick={() => window.location.href = '/login'}>Войти снова</Button>
                </div>
            )}
            <div className="mt-3 small">id: {id}<br/>app: {JSON.stringify(app)}<br/>error: {error}</div>
        </Container>
    );
    if (!app) return <Container className="mt-5"><div className="text-danger">app is null</div><div className="mt-3 small">id: {id}<br/>app: {JSON.stringify(app)}<br/>error: {error}</div></Container>;

    const st = STATUS_META[app.status] || { label: app.status, bg: "secondary", text: "white" };
    const isPending = app.status === "PENDING";
    const isAccepted = app.status === "ACCEPTED";
    const isClosed = app.status === "CLOSED";
    const isRejected = app.status === "REJECTED";

    return (
        <Container className="mt-5" style={{ maxWidth: "600px" }}>
            <Card className="shadow border-0 mb-4">
                <Card.Header className="bg-primary text-white py-3">
                    <div className="d-flex align-items-center gap-3">
                        <Button variant="light" size="sm" onClick={() => navigate(-1)}>&larr; Назад</Button>
                        <h5 className="mb-0">Отклик #{app.id}</h5>
                    </div>
                </Card.Header>
                <Card.Body>
                    <div className="mb-3">
                        <strong>Студент:</strong> {app.studentName || `Студент #${app.studentId || "-"}`}
                        <br />
                        <span className="text-muted small">ID: {app.studentId || "-"}</span>
                        {app.studentEmail && <><br /><a href={`mailto:${app.studentEmail}`}>{app.studentEmail}</a></>}
                        {app.studentPhone && <><br /><span className="text-muted small">Тел: {app.studentPhone}</span></>}
                        {app.studentSkills && <><br /><span className="text-muted small">Навыки: {app.studentSkills}</span></>}
                    </div>
                    <div className="mb-3">
                        <strong>Вакансия:</strong> <Link to={`/vacancy/${app.vacancyId}`}>#{app.vacancyId}</Link>
                    </div>
                    <div className="mb-3">
                        <strong>Статус:</strong> <Badge bg={st.bg} text={st.text}>{st.label}</Badge>
                    </div>
                    <div className="mb-3">
                        <strong>Дата отклика:</strong> {formatDate(app.createdAt)}
                    </div>
                    {isPending && (
                        <div className="mb-3">
                            <Button
                                variant="success"
                                className="me-2"
                                disabled={statusLoading}
                                onClick={() => handleStatus("ACCEPTED")}
                            >
                                {statusLoading ? "..." : "Принять"}
                            </Button>
                            <Button
                                variant="outline-danger"
                                disabled={statusLoading}
                                onClick={() => handleStatus("REJECTED")}
                            >
                                {statusLoading ? "..." : "Отклонить"}
                            </Button>
                            {statusError && <div className="text-danger small mt-2">{statusError}</div>}
                        </div>
                    )}
                    {isAccepted && !app.review && (
                        <div className="mb-3">
                            <strong>Отзыв:</strong>
                            <div>
                                {reviewSuccess && (
                                    <div className="alert alert-success py-2 px-3 mb-2 small">Отзыв успешно сохранён!</div>
                                )}
                                  <Form.Group className="mb-2">
                                      <Form.Label className="mb-1">Оценка (1-100):</Form.Label>
                                      <Form.Control
                                          type="number"
                                          min={1}
                                          max={100}
                                          value={reviewRating}
                                          onChange={e => setReviewRating(e.target.value)}
                                          disabled={reviewLoading}
                                          style={{ width: 100, fontSize: 13, marginBottom: 8 }}
                                      />
                                      <Form.Control
                                          as="textarea"
                                          rows={2}
                                          value={reviewText}
                                          onChange={e => setReviewText(e.target.value)}
                                          placeholder="Оставьте отзыв..."
                                          maxLength={500}
                                          disabled={reviewLoading}
                                          style={{ fontSize: 13 }}
                                      />
                                  </Form.Group>
                                  <Button
                                      size="sm"
                                      variant="primary"
                                      disabled={reviewLoading}
                                      onClick={handleReview}
                                  >
                                      {reviewLoading ? "Сохраняю..." : "Оставить отзыв"}
                                  </Button>
                                  {reviewError && (
                                      <div className="text-danger small mt-1">{reviewError}</div>
                                  )}
                            </div>
                        </div>
                    )}
                    {(isClosed || isRejected || (isAccepted && app.review)) && (
                        <div className="mb-3">
                            <strong>Отзыв:</strong>
                            {app.review && app.review.comment ? (
                                <div className="text-success small mt-1">{app.review.comment} {typeof app.review.rating !== 'undefined' && (<span className="ms-2">Оценка: <b>{app.review.rating}</b></span>)}</div>
                            ) : (
                                <span className="text-muted small">{isClosed ? "Стажировка закрыта." : isRejected ? "Заявка отклонена." : "Отзыв не оставлен."}</span>
                            )}
                        </div>
                    )}
                </Card.Body>
            </Card>
        </Container>
    );
}

