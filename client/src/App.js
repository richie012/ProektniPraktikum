import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Spinner, Container } from "react-bootstrap";
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
    return (
      <Container className="d-flex justify-content-center mt-5">
        <Spinner animation="border" variant="primary" role="status">
          <span className="visually-hidden">Загрузка...</span>
        </Spinner>
      </Container>
    );
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