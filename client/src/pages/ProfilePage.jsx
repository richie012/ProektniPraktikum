import { useAuth } from "../context/AuthContext";
import { Container, Card, Badge, Alert } from "react-bootstrap";

export default function ProfilePage() {
    const { user } = useAuth();

    if (!user) {
        return (
            <Container className="mt-5">
                <Alert variant="warning">
                    ⚠️ Пользователь не авторизован. Пожалуйста, <a href="/login">войдите в систему</a>.
                </Alert>
            </Container>
        );
    }

    const roleLabel = user.role === "STUDENT" ? "🎓 Студент" : user.role === "EMPLOYER" ? "🏢 Работодатель" : user.role;
    const roleBadge = user.role === "STUDENT" ? "success" : user.role === "EMPLOYER" ? "warning" : "secondary";

    return (
        <Container className="mt-5" style={{ maxWidth: "520px" }}>
            <Card className="shadow border-0">
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
                <Card.Body className="p-4">
                    <table className="table table-borderless mb-0">
                        <tbody>
                            <tr>
                                <th scope="row" className="text-muted ps-0" style={{ width: "40%" }}>Email</th>
                                <td>{user.email}</td>
                            </tr>
                            <tr>
                                <th scope="row" className="text-muted ps-0">Роль</th>
                                <td>
                                    <Badge bg={roleBadge} className="fs-6 fw-normal px-3 py-1">
                                        {roleLabel || "Не определена"}
                                    </Badge>
                                </td>
                            </tr>
                            {user.studentId && (
                                <tr>
                                    <th scope="row" className="text-muted ps-0">ID студента</th>
                                    <td><code>{user.studentId}</code></td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </Card.Body>
            </Card>
        </Container>
    );
}
