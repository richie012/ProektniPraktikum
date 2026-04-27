import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import API from "../api/api";
import {
    Container, Card, Badge, Alert, Spinner,
    Form, InputGroup, Button, Tabs, Tab, Table,
} from "react-bootstrap";

// ── вспомогательные функции ───────────────────────────────────────────
const STATUS_META = {
    PENDING:  { label: "На рассмотрении", bg: "warning",  text: "dark" },
    ACCEPTED: { label: "Принята",          bg: "success",  text: "white" },
    REJECTED: { label: "Отклонена",        bg: "danger",   text: "white" },
};

function formatDate(iso) {
    if (!iso) return "—";
    return new Date(iso).toLocaleString("ru-RU", {
        day: "2-digit", month: "2-digit", year: "numeric",
        hour: "2-digit", minute: "2-digit",
    });
}

// ── компонент ────────────────────────────────────────────────────────
export default function ProfilePage() {
    const { user } = useAuth();
    const [searchParams, setSearchParams] = useSearchParams();

    /* ── резюме ── */
    const [resume, setResume]           = useState(null);
    const [resumeLoading, setResumeLoading] = useState(false);
    const [resumeUrl, setResumeUrl]     = useState("");
    const [saveStatus, setSaveStatus]   = useState(null);
    const [saving, setSaving]           = useState(false);

    /* ── заявки ── */
    const [applications, setApplications]       = useState([]);
    const [appsLoading, setAppsLoading]         = useState(false);
    const [appsError, setAppsError]             = useState("");

    const isStudent = Boolean(user?.role === "STUDENT" && user?.studentId);
    const isEmployer = Boolean(user?.role === "EMPLOYER" && user?.employerId);
    const canViewApplications = isStudent || isEmployer;
    const requestedTab = searchParams.get("tab");
    const allowedTabs = ["profile", "resume", "applications"];
    const normalizedRequestedTab = allowedTabs.includes(requestedTab) ? requestedTab : "profile";
    const initialTab =
        normalizedRequestedTab === "resume" && !isStudent
            ? "profile"
            : normalizedRequestedTab === "applications" && !canViewApplications
                ? "profile"
                : normalizedRequestedTab;
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

    /* ── загрузка резюме ── */
    useEffect(() => {
        if (!isStudent) return;
        setResumeLoading(true);
        API.get("/resume", { params: { studentId: user.studentId } })
            .then(res => { setResume(res.data); setResumeUrl(res.data.fileUrl || ""); })
            .catch(err => { if (err?.response?.status !== 404) console.error(err); setResume(null); })
            .finally(() => setResumeLoading(false));
    }, [user?.studentId]); // eslint-disable-line

    /* ── загрузка заявок ── */
    useEffect(() => {
        if (!canViewApplications) return;

        const params = isStudent
            ? { studentId: user.studentId }
            : { employerId: user.employerId };

        setAppsLoading(true);
        API.get("/applications", { params })
            .then(res => { setApplications(Array.isArray(res.data) ? res.data : []); setAppsError(""); })
            .catch(() => setAppsError("Не удалось загрузить заявки. Проверьте соединение с сервером."))
            .finally(() => setAppsLoading(false));
    }, [user?.studentId, user?.employerId, canViewApplications, isStudent]);

    /* ── сохранение резюме ── */
    const handleSaveResume = async (e) => {
        e.preventDefault();
        if (!resumeUrl.trim()) return;
        setSaving(true); setSaveStatus(null);
        try {
            const res = await API.post("/resume", null, {
                params: { studentId: user.studentId, fileUrl: resumeUrl.trim() },
            });
            setResume({ id: res.data.id, fileUrl: resumeUrl.trim() });
            setSaveStatus("ok");
        } catch { setSaveStatus("err"); }
        finally  { setSaving(false); }
    };

    // ── не авторизован ──
    if (!user) {
        return (
            <Container className="mt-5">
                <Alert variant="warning">
                    ⚠️ Пользователь не авторизован. Пожалуйста, <a href="/login">войдите в систему</a>.
                </Alert>
            </Container>
        );
    }

    const roleLabel = user.role === "STUDENT" ? "🎓 Студент"
                    : user.role === "EMPLOYER" ? "🏢 Работодатель"
                    : user.role;
    const roleBadge = user.role === "STUDENT" ? "success"
                    : user.role === "EMPLOYER" ? "warning"
                    : "secondary";

    return (
        <Container className="mt-5" style={{ maxWidth: "680px" }}>
            {/* ── шапка ── */}
            <Card className="shadow border-0 mb-4">
                <Card.Header className="bg-primary text-white py-3">
                    <div className="d-flex align-items-center gap-3">
                        <div
                            className="rounded-circle bg-white text-primary d-flex align-items-center justify-content-center"
                            style={{ width: 52, height: 52, fontSize: 24, flexShrink: 0 }}
                        >
                            👤
                        </div>
                        <div>
                            <h5 className="mb-0">Личный кабинет</h5>
                            <small className="text-white-50">{user.email}</small>
                        </div>
                    </div>
                </Card.Header>
            </Card>

            {/* ── вкладки ── */}
            <Tabs activeKey={activeTab} onSelect={handleTabSelect} className="mb-3" fill>

                {/* ══ ВКЛАДКА: Профиль ══ */}
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
                                            <Badge bg={roleBadge} className="fs-6 fw-normal px-3 py-1">
                                                {roleLabel || "Не определена"}
                                            </Badge>
                                        </td>
                                    </tr>
                                    {user.studentId && (
                                        <tr>
                                            <th className="text-muted ps-0">ID студента</th>
                                            <td><code>{user.studentId}</code></td>
                                        </tr>
                                    )}
                                    {user.employerId && (
                                        <tr>
                                            <th className="text-muted ps-0">ID работодателя</th>
                                            <td><code>{user.employerId}</code></td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </Card.Body>
                    </Card>
                </Tab>

                {/* ══ ВКЛАДКА: Резюме (только студент) ══ */}
                {isStudent && (
                    <Tab eventKey="resume" title="📄 Резюме">
                        <Card className="shadow border-0">
                            <Card.Body className="p-4">
                                {resumeLoading ? (
                                    <div className="d-flex align-items-center gap-2 text-muted mb-3">
                                        <Spinner animation="border" size="sm" />
                                        <span>Загрузка резюме...</span>
                                    </div>
                                ) : resume?.fileUrl ? (
                                    <div className="mb-3">
                                        <div className="text-muted small mb-1">Прикреплённое резюме:</div>
                                        <a
                                            href={resume.fileUrl}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="d-inline-flex align-items-center gap-1 text-primary fw-semibold"
                                            style={{ wordBreak: "break-all" }}
                                        >
                                            🔗 {resume.fileUrl}
                                        </a>
                                    </div>
                                ) : (
                                    <Alert variant="secondary" className="py-2 mb-3">
                                        Резюме ещё не прикреплено.
                                    </Alert>
                                )}

                                <hr className="my-3" />
                                <p className="fw-semibold mb-2">
                                    {resume ? "Обновить ссылку на резюме" : "Добавить ссылку на резюме"}
                                </p>
                                <Form onSubmit={handleSaveResume}>
                                    <Form.Group className="mb-2">
                                        <div className="small text-muted mb-1">
                                            Вставьте прямую ссылку (Google Drive, HH.ru, PDF и т.д.)
                                        </div>
                                        <InputGroup>
                                            <Form.Control
                                                type="url"
                                                placeholder="https://drive.google.com/..."
                                                value={resumeUrl}
                                                onChange={e => { setResumeUrl(e.target.value); setSaveStatus(null); }}
                                                required
                                            />
                                            <Button type="submit" variant="primary" disabled={saving}>
                                                {saving ? <Spinner animation="border" size="sm" /> : "Сохранить"}
                                            </Button>
                                        </InputGroup>
                                    </Form.Group>
                                    {saveStatus === "ok" && (
                                        <Alert variant="success" className="py-2 mt-2 mb-0">✅ Резюме успешно сохранено!</Alert>
                                    )}
                                    {saveStatus === "err" && (
                                        <Alert variant="danger" className="py-2 mt-2 mb-0">❌ Не удалось сохранить резюме.</Alert>
                                    )}
                                </Form>
                            </Card.Body>
                        </Card>
                    </Tab>
                )}

                {/* ══ ВКЛАДКА: Заявки (студент / работодатель) ══ */}
                {canViewApplications && (
                    <Tab
                        eventKey="applications"
                        title={
                            <span>
                                {isStudent ? "📋 Мои заявки" : "📬 Отклики на мои вакансии"}{" "}
                                {!appsLoading && applications.length > 0 && (
                                    <Badge bg="primary" pill className="ms-1">
                                        {applications.length}
                                    </Badge>
                                )}
                            </span>
                        }
                    >
                        <Card className="shadow border-0">
                            <Card.Body className="p-4">
                                {appsLoading && (
                                    <div className="d-flex align-items-center gap-2 text-muted">
                                        <Spinner animation="border" size="sm" />
                                        <span>Загрузка заявок...</span>
                                    </div>
                                )}
                                {!appsLoading && appsError && (
                                    <Alert variant="danger">⚠️ {appsError}</Alert>
                                )}
                                {!appsLoading && !appsError && applications.length === 0 && (
                                    <Alert variant="info">
                                        {isStudent ? (
                                            <>
                                                ℹ️ Вы ещё не подавали заявок.{" "}
                                                <Link to="/">Посмотреть вакансии →</Link>
                                            </>
                                        ) : (
                                            <>ℹ️ На ваши вакансии пока нет откликов.</>
                                        )}
                                    </Alert>
                                )}
                                {!appsLoading && !appsError && applications.length > 0 && (
                                    <Table hover responsive className="mb-0 align-middle">
                                        <thead className="table-light">
                                            <tr>
                                                <th>#</th>
                                                {!isStudent && <th>Студент</th>}
                                                <th>Вакансия</th>
                                                <th>Статус</th>
                                                <th>Дата подачи</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {applications.map((app, idx) => {
                                                const st = STATUS_META[app.status] || { label: app.status, bg: "secondary", text: "white" };
                                                return (
                                                    <tr key={app.id}>
                                                        <td className="text-muted small">{idx + 1}</td>
                                                        {!isStudent && <td className="text-muted small">#{app.studentId || "—"}</td>}
                                                        <td>
                                                            <Link
                                                                to={`/vacancy/${app.vacancyId}`}
                                                                className="text-primary fw-semibold text-decoration-none"
                                                            >
                                                                Вакансия #{app.vacancyId}
                                                            </Link>
                                                        </td>
                                                        <td>
                                                            <Badge bg={st.bg} text={st.text} className="px-2 py-1">
                                                                {st.label}
                                                            </Badge>
                                                        </td>
                                                        <td className="text-muted small">{formatDate(app.createdAt)}</td>
                                                    </tr>
                                                );
                                            })}
                                        </tbody>
                                    </Table>
                                )}
                            </Card.Body>
                        </Card>
                    </Tab>
                )}

            </Tabs>
        </Container>
    );
}
