import { Link } from "react-router-dom";
import { Card, Badge, Button } from "react-bootstrap";

export default function VacancyCard({ vacancy }) {
    return (
        <Card className="mb-3 shadow-sm border-0">
            <Card.Body>
                <div className="d-flex justify-content-between align-items-start">
                    <div>
                        <Card.Title className="mb-1 text-primary">
                            {vacancy.title}
                        </Card.Title>
                        <Card.Subtitle className="mb-2">
                            <Badge bg="info" text="dark">
                                🏢 {vacancy.companyName}
                            </Badge>
                        </Card.Subtitle>
                        {vacancy.description && (
                            <Card.Text className="text-muted small">
                                {vacancy.description.length > 120
                                    ? vacancy.description.slice(0, 120) + "..."
                                    : vacancy.description}
                            </Card.Text>
                        )}
                    </div>
                </div>
                <Button
                    as={Link}
                    to={`/vacancy/${vacancy.id}`}
                    variant="outline-primary"
                    size="sm"
                    className="mt-1"
                >
                    Подробнее →
                </Button>
            </Card.Body>
        </Card>
    );
}