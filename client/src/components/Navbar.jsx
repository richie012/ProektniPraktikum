import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Navbar as BsNavbar, Nav, Container, Button } from "react-bootstrap";

export default function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <BsNavbar bg="primary" variant="dark" expand="md" className="shadow-sm">
            <Container>
                <BsNavbar.Brand as={Link} to="/" className="fw-bold fs-5">
                    🎓 Стажировки
                </BsNavbar.Brand>
                <BsNavbar.Toggle aria-controls="main-navbar" />
                <BsNavbar.Collapse id="main-navbar">
                    <Nav className="ms-auto align-items-md-center gap-2">
                        {user ? (
                            <>
                                <Nav.Link as={Link} to="/" className="text-white-50">
                                    Вакансии
                                </Nav.Link>
                                <Nav.Link as={Link} to="/profile" className="text-white">
                                    👤 Профиль
                                </Nav.Link>
                                <span className="navbar-text text-info small me-1">
                                    {user.email}
                                </span>
                                <Button
                                    variant="outline-light"
                                    size="sm"
                                    onClick={handleLogout}
                                >
                                    Выйти
                                </Button>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/login" className="text-white">
                                    Войти
                                </Nav.Link>
                                <Nav.Link as={Link} to="/register" className="text-white">
                                    Регистрация
                                </Nav.Link>
                            </>
                        )}
                    </Nav>
                </BsNavbar.Collapse>
            </Container>
        </BsNavbar>
    );
}