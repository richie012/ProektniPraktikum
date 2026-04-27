import { useAuth } from "../context/AuthContext";

export default function ProfilePage() {
    const { user } = useAuth();

    if (!user) {
        return <p style={{ padding: "20px" }}>Пользователь не авторизован.</p>;
    }

    return (
        <div style={{ padding: "20px" }}>
            <h2>Профиль</h2>
            <p><b>Email:</b> {user.email}</p>
            <p><b>Роль:</b> {user.role || "Не загружена"}</p>
            {user.studentId && <p><b>ID студента:</b> {user.studentId}</p>}
        </div>
    );
}

