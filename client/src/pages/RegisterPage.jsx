import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function RegisterPage() {
    const { register } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("STUDENT");

    const handleRegister = async () => {
        try {
            await register(email, password, role);
            alert("Аккаунт создан");
            navigate("/profile");
        } catch (error) {
            if (error?.response?.status === 409) {
                alert("Этот email уже зарегистрирован");
                return;
            }
            if (error?.message === "TOKEN_MISSING") {
                alert("Сервер не вернул JWT токен. Перезапустите backend на актуальной версии.");
                return;
            }
            alert("Ошибка регистрации. Проверьте введённые данные и доступность сервера.");
        }
    };

    return (
        <div>
            <h2>Регистрация</h2>

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

            <select value={role} onChange={e => setRole(e.target.value)}>
                <option value="STUDENT">Студент</option>
                <option value="EMPLOYER">Работник</option>
            </select>

            <br />

            <button onClick={handleRegister}>Создать аккаунт</button>
        </div>
    );
}