import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";
import { Container, Card, Form, Button, Alert } from "react-bootstrap";

export default function RegisterPage() {
    const { register } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("STUDENT");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleRegister = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);
        try {
            await register(email, password, role);
            navigate("/profile");
        } catch (err) {
            if (err?.response?.status === 409) {
                setError("Этот email уже зарегистрирован");
            } else if (err?.message === "TOKEN_MISSING") {
                setError("Сервер не вернул JWT токен. Перезапустите backend на актуальной версии.");
            } else {
                setError("Ошибка регистрации. Проверьте введённые данные и доступность сервера.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="d-flex justify-content-center align-items-start" style={{ minHeight: "80vh", paddingTop: "60px" }}>
            <Card className="shadow" style={{ width: "100%", maxWidth: "420px" }}>
                <Card.Header className="bg-primary text-white text-center py-3">
                    <h4 className="mb-0">📝 Регистрация</h4>
                </Card.Header>
                <Card.Body className="p-4">
                    {error && <Alert variant="danger">{error}</Alert>}
                    <Form onSubmit={handleRegister}>
                        <Form.Group className="mb-3" controlId="regEmail">
                            <Form.Label>Адрес электронной почты</Form.Label>
                            <Form.Control
                                type="email"
                                placeholder="example@mail.ru"
                                value={email}
                                onChange={e => setEmail(e.target.value)}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="regPassword">
                            <Form.Label>Пароль</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="Придумайте надёжный пароль"
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-4" controlId="regRole">
                            <Form.Label>Роль в системе</Form.Label>
                            <Form.Select
                                value={role}
                                onChange={e => setRole(e.target.value)}
                            >
                                <option value="STUDENT">🎓 Студент — ищу стажировку</option>
                                <option value="EMPLOYER">🏢 Работодатель — размещаю вакансии</option>
                            </Form.Select>
                            <Form.Text className="text-muted">
                                Роль определяет ваши возможности в системе
                            </Form.Text>
                        </Form.Group>

                        <Button
                            type="submit"
                            variant="primary"
                            className="w-100"
                            disabled={loading}
                        >
                            {loading ? "Создание аккаунта..." : "Создать аккаунт"}
                        </Button>
                    </Form>
                </Card.Body>
                <Card.Footer className="text-center text-muted small py-3">
                    Уже есть аккаунт?{" "}
                    <Link to="/login" className="text-primary fw-semibold">
                        Войти
                    </Link>
                </Card.Footer>
            </Card>
        </Container>
    );
}