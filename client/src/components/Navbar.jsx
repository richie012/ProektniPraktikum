import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
    const { user, logout } = useAuth();

    const handleLogout = () => {
        logout();
    };

    return (
        <div style={{
            display: "flex",
            justifyContent: "space-between",
            padding: "10px",
            borderBottom: "1px solid gray"
        }}>
            <Link to="/">Internships</Link>

            <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                {user ? (
                    <>
                        <Link to="/profile">Профиль</Link>
                        <span>{user.email}</span>
                        <button type="button" onClick={handleLogout}>Выйти</button>
                    </>
                ) : (
                    <>
                        <Link to="/login">Войти</Link>
                        {" | "}
                        <Link to="/register">Регистрация</Link>
                    </>
                )}
            </div>
        </div>
    );
}