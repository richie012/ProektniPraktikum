import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleLogin = async () => {
        try {
            await login(email, password);
            navigate("/profile");
        } catch (error) {
            if (error?.response?.status === 401) {
                alert("Неверный логин или пароль");
                return;
            }
            if (error?.message === "TOKEN_MISSING") {
                alert("Сервер не вернул JWT токен. Перезапустите backend на актуальной версии.");
                return;
            }
            alert("Ошибка входа. Проверьте введённые данные и доступность сервера.");
        }
    };

    return (
        <div>
            <h2>Вход</h2>

            <input
                placeholder="Email"
                value={email}
                onChange={e => setEmail(e.target.value)}
            />

            <br />

            <input
                type="password"
                placeholder="Пароль"
                value={password}
                onChange={e => setPassword(e.target.value)}
            />

            <br />

            <button onClick={handleLogin}>Войти</button>
        </div>
    );
}