import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import { useAuth } from "./context/AuthContext";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import VacanciesPage from "./pages/VacanciesPage";
import VacancyPage from "./pages/VacancyPage";
import ProfilePage from "./pages/ProfilePage";

function RequireAuth({ children }) {
  const { user, isAuthLoading } = useAuth();

  if (isAuthLoading) {
    return <p style={{ padding: "20px" }}>Загрузка...</p>;
  }

  return user ? children : <Navigate to="/login" replace />;
}

function App() {
  return (
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/" element={<VacanciesPage />} />
          <Route path="/vacancy/:id" element={<VacancyPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/profile"
            element={(
              <RequireAuth>
                <ProfilePage />
              </RequireAuth>
            )}
          />
        </Routes>
      </BrowserRouter>
  );
}

export default App;