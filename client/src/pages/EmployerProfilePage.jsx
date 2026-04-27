import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import API from "../api/api";
import { Container, Card, Badge, Alert, Spinner, Tabs, Tab, Table, Form, Button } from "react-bootstrap";

const STATUS_META = {
    PENDING: { label: "На рассмотрении", bg: "warning", text: "dark" },
    ACCEPTED: { label: "Принята", bg: "success", text: "white" },
    REJECTED: { label: "Отклонена", bg: "danger", text: "white" },
};

function formatDate(iso) {
    if (!iso) return "-";
    return new Date(iso).toLocaleString("ru-RU", {
        day: "2-digit", month: "2-digit", year: "numeric",
        hour: "2-digit", minute: "2-digit",
    });
}

export default function EmployerProfilePage({ user }) {
    const [searchParams, setSearchParams] = useSearchParams();

    const [applications, setApplications] = useState([]);
    const [appsLoading, setAppsLoading] = useState(false);
    const [appsError, setAppsError] = useState("");
    const [statusActionLoadingId, setStatusActionLoadingId] = useState(null);
    const [statusActionError, setStatusActionError] = useState("");

    const [vacancyTitle, setVacancyTitle] = useState("");
    const [vacancyDescription, setVacancyDescription] = useState("");
    const [createLoading, setCreateLoading] = useState(false);
    const [createError, setCreateError] = useState("");
    const [createdVacancy, setCreatedVacancy] = useState(null);

    const requestedTab = searchParams.get("tab");
    const allowedTabs = ["profile", "applications", "create-vacancy"];
    const initialTab = allowedTabs.includes(requestedTab) ? requestedTab : "profile";
    const [activeTab, setActiveTab] = useState(initialTab);

    useEffect(() => {
        setActiveTab(initialTab);
    }, [initialTab]);

    const handleTabSelect = (key) => {
        if (!key) return;
        setActiveTab(key);
        const nextParams = new URLSearchParams(searchParams);
        if (key === "profile") {
            nextParams.delete("tab");
        } else {
            nextParams.set("tab", key);
        }
        setSearchParams(nextParams, { replace: true });
    };

    useEffect(() => {
        if (!user.employerId) {
            setAppsError("ID работодателя пока не загружен. Обновите страницу.");
            setApplications([]);
            return;
        }

        setAppsLoading(true);
        setAppsError("");
        API.get("/applications", { params: { employerId: user.employerId } })
            .then((res) => {
                setApplications(Array.isArray(res.data) ? res.data : []);
                setAppsError("");
            })
            .catch(() => setAppsError("Не удалось загрузить отклики. Проверьте соединение с сервером."))
            .finally(() => setAppsLoading(false));
    }, [user.employerId]);

    const handleCreateVacancy = async (event) => {
        event.preventDefault();

        setCreateError("");
        setCreatedVacancy(null);

        if (!vacancyTitle.trim() || !vacancyDescription.trim()) {
            setCreateError("Заполните название и описание вакансии.");
            return;
        }

        setCreateLoading(true);
        try {
            const res = await API.post("/vacancies", {
                title: vacancyTitle.trim(),
                description: vacancyDescription.trim(),
            });

            setCreatedVacancy(res.data);
            setVacancyTitle("");
            setVacancyDescription("");
        } catch (err) {
            if (err?.response?.status === 403) {
                setCreateError("Только работодатель может создавать вакансии.");
            } else {
                setCreateError("Не удалось создать вакансию. Попробуйте позже.");
            }
        } finally {
            setCreateLoading(false);
        }
    };

    const handleUpdateApplicationStatus = async (applicationId, status) => {
        setStatusActionError("");
        setStatusActionLoadingId(applicationId);

        try {
            const res = await API.patch(`/applications/${applicationId}/status`, { status });
            setApplications((prev) => prev.map((item) => (
                item.id === applicationId ? { ...item, status: res.data.status } : item
            )));
        } catch (err) {
            if (err?.response?.status === 409) {
                setStatusActionError("Эту заявку уже обработали ранее. Обновите список.");
            } else if (err?.response?.status === 403) {
                setStatusActionError("Нельзя изменить статус чужой заявки.");
            } else {
                setStatusActionError("Не удалось изменить статус заявки. Попробуйте позже.");
            }
        } finally {
            setStatusActionLoadingId(null);
        }
    };

    return (
        <Container className="mt-5" style={{ maxWidth: "680px" }}>
            <Card className="shadow border-0 mb-4">
                <Card.Header className="bg-primary text-white py-3">
                    <div className="d-flex align-items-center gap-3">
                        <div
                            className="rounded-circle bg-white text-primary d-flex align-items-center justify-content-center"
                            style={{ width: 52, height: 52, fontSize: 24, flexShrink: 0 }}
                        >
                            🏢
                        </div>
                        <div>
                            <h5 className="mb-0">Кабинет работодателя</h5>
                            <small className="text-white-50">{user.email}</small>
                        </div>
                    </div>
                </Card.Header>
            </Card>

            <Tabs activeKey={activeTab} onSelect={handleTabSelect} className="mb-3" fill>
                <Tab eventKey="profile" title="👤 Профиль">
                    <Card className="shadow border-0">
                        <Card.Body className="p-4">
                            <table className="table table-borderless mb-0">
                                <tbody>
                                    <tr>
                                        <th className="text-muted ps-0" style={{ width: "40%" }}>Email</th>
                                        <td>{user.email}</td>
                                    </tr>
                                    <tr>
                                        <th className="text-muted ps-0">Роль</th>
                                        <td>
                                            <Badge bg="warning" text="dark" className="fs-6 fw-normal px-3 py-1">🏢 Работодатель</Badge>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th className="text-muted ps-0">ID работодателя</th>
                                        <td><code>{user.employerId}</code></td>
                                    </tr>
                                </tbody>
                            </table>
                        </Card.Body>
                    </Card>
                </Tab>

                <Tab
                    eventKey="applications"
                    title={
                        <span>
                            📬 Отклики {" "}
                            {!appsLoading && applications.length > 0 && (
                                <Badge bg="primary" pill className="ms-1">{applications.length}</Badge>
                            )}
                        </span>
                    }
                >
                    <Card className="shadow border-0">
                        <Card.Body className="p-4">
                            {appsLoading && (
                                <div className="d-flex align-items-center gap-2 text-muted">
                                    <Spinner animation="border" size="sm" />
                                    <span>Загрузка откликов...</span>
                                </div>
                            )}
                            {!appsLoading && appsError && <Alert variant="danger">⚠️ {appsError}</Alert>}
                            {!appsLoading && !appsError && statusActionError && (
                                <Alert variant="danger">⚠️ {statusActionError}</Alert>
                            )}
                            {!appsLoading && !appsError && applications.length === 0 && (
                                <Alert variant="info">ℹ️ На ваши вакансии пока нет откликов.</Alert>
                            )}
                            {!appsLoading && !appsError && applications.length > 0 && (
                                <Table hover responsive className="mb-0 align-middle">
                                    <thead className="table-light">
                                        <tr>
                                            <th>#</th>
                                            <th>Студент</th>
                                            <th>Вакансия</th>
                                            <th>Статус</th>
                                            <th>Дата подачи</th>
                                            <th>Действия</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {applications.map((app, idx) => {
                                            const st = STATUS_META[app.status] || { label: app.status, bg: "secondary", text: "white" };
                                            const isPending = app.status === "PENDING";
                                            const isProcessing = statusActionLoadingId === app.id;
                                            return (
                                                <tr key={app.id}>
                                                    <td className="text-muted small">{idx + 1}</td>
                                                    <td className="text-muted small">#{app.studentId || "-"}</td>
                                                    <td>
                                                        <Link
                                                            to={`/vacancy/${app.vacancyId}`}
                                                            className="text-primary fw-semibold text-decoration-none"
                                                        >
                                                            Вакансия #{app.vacancyId}
                                                        </Link>
                                                    </td>
                                                    <td>
                                                        <Badge bg={st.bg} text={st.text} className="px-2 py-1">{st.label}</Badge>
                                                    </td>
                                                    <td className="text-muted small">{formatDate(app.createdAt)}</td>
                                                    <td>
                                                        {isPending ? (
                                                            <div className="d-flex gap-2">
                                                                <Button
                                                                    size="sm"
                                                                    variant="success"
                                                                    disabled={isProcessing}
                                                                    onClick={() => handleUpdateApplicationStatus(app.id, "ACCEPTED")}
                                                                >
                                                                    {isProcessing ? "..." : "Принять"}
                                                                </Button>
                                                                <Button
                                                                    size="sm"
                                                                    variant="outline-danger"
                                                                    disabled={isProcessing}
                                                                    onClick={() => handleUpdateApplicationStatus(app.id, "REJECTED")}
                                                                >
                                                                    {isProcessing ? "..." : "Отклонить"}
                                                                </Button>
                                                            </div>
                                                        ) : (
                                                            <span className="text-muted small">Обработано</span>
                                                        )}
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </Table>
                            )}
                        </Card.Body>
                    </Card>
                </Tab>

                <Tab eventKey="create-vacancy" title="➕ Создать вакансию">
                    <Card className="shadow border-0">
                        <Card.Body className="p-4">
                            <h6 className="mb-3 text-primary">Новая вакансия</h6>

                            <Form onSubmit={handleCreateVacancy}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Название</Form.Label>
                                    <Form.Control
                                        value={vacancyTitle}
                                        onChange={(e) => setVacancyTitle(e.target.value)}
                                        placeholder="Например: Java Intern"
                                        maxLength={255}
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Описание</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={5}
                                        value={vacancyDescription}
                                        onChange={(e) => setVacancyDescription(e.target.value)}
                                        placeholder="Опишите обязанности, требования и условия"
                                        maxLength={2000}
                                        required
                                    />
                                </Form.Group>

                                <Button type="submit" variant="primary" disabled={createLoading}>
                                    {createLoading ? "Создание..." : "Создать вакансию"}
                                </Button>
                            </Form>

                            {createError && (
                                <Alert variant="danger" className="mt-3 mb-0">⚠️ {createError}</Alert>
                            )}

                            {createdVacancy && (
                                <Alert variant="success" className="mt-3 mb-0">
                                    ✅ Вакансия создана: <strong>{createdVacancy.title}</strong>.{" "}
                                    <Link to={`/vacancy/${createdVacancy.id}`}>Открыть карточку →</Link>
                                </Alert>
                            )}
                        </Card.Body>
                    </Card>
                </Tab>
            </Tabs>
        </Container>
    );
}

