import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";
import { Container, Card, Form, Button, Alert } from "react-bootstrap";

export default function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);
        try {
            await login(email, password);
            navigate("/profile");
        } catch (err) {
            if (err?.response?.status === 401) {
                setError("Неверный логин или пароль");
            } else if (err?.message === "TOKEN_MISSING") {
                setError("Сервер не вернул JWT токен. Перезапустите backend на актуальной версии.");
            } else {
                setError("Ошибка входа. Проверьте введённые данные и доступность сервера.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="d-flex justify-content-center align-items-start" style={{ minHeight: "80vh", paddingTop: "60px" }}>
            <Card className="shadow" style={{ width: "100%", maxWidth: "420px" }}>
                <Card.Header className="bg-primary text-white text-center py-3">
                    <h4 className="mb-0">🔐 Вход в систему</h4>
                </Card.Header>
                <Card.Body className="p-4">
                    {error && <Alert variant="danger">{error}</Alert>}
                    <Form onSubmit={handleLogin}>
                        <Form.Group className="mb-3" controlId="loginEmail">
                            <Form.Label>Адрес электронной почты</Form.Label>
                            <Form.Control
                                type="email"
                                placeholder="example@mail.ru"
                                value={email}
                                onChange={e => setEmail(e.target.value)}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-4" controlId="loginPassword">
                            <Form.Label>Пароль</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="Введите пароль"
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                required
                            />
                        </Form.Group>

                        <Button
                            type="submit"
                            variant="primary"
                            className="w-100"
                            disabled={loading}
                        >
                            {loading ? "Вход..." : "Войти"}
                        </Button>
                    </Form>
                </Card.Body>
                <Card.Footer className="text-center text-muted small py-3">
                    Нет аккаунта?{" "}
                    <Link to="/register" className="text-primary fw-semibold">
                        Зарегистрироваться
                    </Link>
                </Card.Footer>
            </Card>
        </Container>
    );
}