import { BrowserRouter, Routes, Route } from "react-router-dom";
import VacanciesPage from "./pages/VacanciesPage";
import VacancyPage from "./pages/VacancyPage";

function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<VacanciesPage />} />
          <Route path="/vacancy/:id" element={<VacancyPage />} />
        </Routes>
      </BrowserRouter>
  );
}

export default App;