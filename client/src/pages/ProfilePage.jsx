import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Container, Alert } from "react-bootstrap";
import StudentProfilePage from "./StudentProfilePage";
import EmployerProfilePage from "./EmployerProfilePage";

export default function ProfilePage() {
    const { user } = useAuth();

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

    if (user.role === "STUDENT" && user.studentId) {
        return <StudentProfilePage user={user} />;
    }

    if (user.role === "EMPLOYER") {
        return <EmployerProfilePage user={user} />;
    }

    return (
        <Container className="mt-5">
            <Alert variant="info">
                Профиль загружен, но данные роли неполные. Попробуйте выйти и войти снова.
            </Alert>
            <Link to="/" className="btn btn-outline-primary btn-sm">
                На главную
            </Link>
        </Container>
    );
}
