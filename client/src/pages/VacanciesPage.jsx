import { useEffect, useState } from "react";
import API from "../api/api";
import VacancyCard from "../components/VacancyCard";
import { Container, Spinner, Alert, Form, InputGroup, Button, Row, Col, Pagination } from "react-bootstrap";

export default function VacanciesPage() {
    const [vacancies, setVacancies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [searchInput, setSearchInput] = useState("");
    const [activeSearch, setActiveSearch] = useState("");
    const [currentPage, setCurrentPage] = useState(1);
    const pageSize = 6;

    const loadVacancies = async (search = "") => {
        const normalizedSearch = search.trim();

        setLoading(true);
        setError("");
        setActiveSearch(normalizedSearch);

        try {
            const res = await API.get("/vacancies", {
                params: normalizedSearch ? { search: normalizedSearch } : {},
            });

            setVacancies(Array.isArray(res.data) ? res.data : []);
        } catch (err) {
            console.error(err);
            setVacancies([]);
            setError("Не удалось загрузить вакансии. Проверьте, что сервер запущен.");
        } finally {
            setLoading(false);
        }
    };

    const paginatedVacancies = vacancies.slice((currentPage - 1) * pageSize, currentPage * pageSize);
    const totalPages = Math.ceil(vacancies.length / pageSize);

    const handlePageChange = (page) => {
        setCurrentPage(page);
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    useEffect(() => {
        loadVacancies();
    }, []);

    useEffect(() => {
        setCurrentPage(1);
    }, [vacancies.length, activeSearch]);

    const handleSearchSubmit = (event) => {
        event.preventDefault();
        loadVacancies(searchInput);
    };

    const handleSearchReset = () => {
        setSearchInput("");
        loadVacancies("");
    };

    return (
        <Container className="py-4" style={{ maxWidth: "860px" }}>
            <h2 className="mb-4 pb-2 border-bottom border-primary text-primary">
                📋 Доступные стажировки
            </h2>

            <Form onSubmit={handleSearchSubmit} className="mb-4">
                <div className="fw-semibold text-primary mb-2">
                    Поиск по названию и описанию вакансии
                </div>
                <InputGroup>
                    <Form.Control
                        type="search"
                        placeholder="Например: Java, React, аналитик"
                        value={searchInput}
                        onChange={(event) => setSearchInput(event.target.value)}
                        aria-label="Поисковая строка вакансий"
                    />
                    <Button type="submit" variant="primary" disabled={loading}>
                        Найти
                    </Button>
                    <Button
                        type="button"
                        variant="outline-secondary"
                        onClick={handleSearchReset}
                        disabled={loading && !activeSearch && !searchInput}
                    >
                        Сбросить
                    </Button>
                </InputGroup>
                <Form.Text className="text-muted">
                    Поиск вызывает backend-метод `GET /api/vacancies?search=...`.
                </Form.Text>
            </Form>

            {loading && (
                <div className="d-flex align-items-center gap-2 text-primary">
                    <Spinner animation="border" size="sm" role="status" />
                    <span>Загрузка вакансий...</span>
                </div>
            )}

            {!loading && error && (
                <Alert variant="danger" className="d-flex align-items-center gap-2">
                    ⚠️ {error}
                </Alert>
            )}

            {!loading && !error && vacancies.length === 0 && activeSearch && (
                <Alert variant="info">
                    ℹ️ По запросу <strong>{activeSearch}</strong> вакансий не найдено.
                </Alert>
            )}

            {!loading && !error && vacancies.length === 0 && !activeSearch && (
                <Alert variant="info">
                    ℹ️ Вакансий пока нет. Загляните позже.
                </Alert>
            )}

            <Row xs={1} sm={2} md={2} lg={3} className="g-3">
                {paginatedVacancies.map(v => (
                    <Col key={v.id}>
                        <VacancyCard vacancy={v} />
                    </Col>
                ))}
            </Row>

            {totalPages > 1 && (
                <div className="d-flex justify-content-center mt-4">
                    <Pagination>
                        <Pagination.Prev
                            disabled={currentPage === 1}
                            onClick={() => handlePageChange(currentPage - 1)}
                        />
                        {[...Array(totalPages)].map((_, idx) => (
                            <Pagination.Item
                                key={idx + 1}
                                active={currentPage === idx + 1}
                                onClick={() => handlePageChange(idx + 1)}
                            >
                                {idx + 1}
                            </Pagination.Item>
                        ))}
                        <Pagination.Next
                            disabled={currentPage === totalPages}
                            onClick={() => handlePageChange(currentPage + 1)}
                        />
                    </Pagination>
                </div>
            )}
        </Container>
    );
}