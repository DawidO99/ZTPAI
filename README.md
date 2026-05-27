# F1 Paddock API

## Temat projektu
System zarządzania danymi o kierowcach Formuły 1.
Aplikacja symuluje zaplecze (backend) dla portalu sportowego lub bazy danych FIA, gdzie administratorzy mogą dodawać nowych kierowców, aktualizować ich punkty i przypisywać ich do zespołów.

## Linki
- **Repozytorium GitHub:** [Github Repository Link (Wstaw Link)]()
- **Demo Video:** [YouTube/Loom Link (Wstaw Link)]()

## Architektura (Ocena 3.0 / 4.0 / 5.0)
Aplikacja oparta na architekturze wielowarstwowej:
- **Controller Layer (`controller/`)**: Odbiera zapytania HTTP, zwraca dane (DTO).
- **Service Layer (`service/`)**: Zawiera główną mechanikę biznesową.
- **Repository Layer (`repository/`)**: Bezpośrednia interakcja z bazą danych przez Spring Data JPA.
- **DTO (`dto/`) i Mapper (`model/Mapper`)**: Rozdzielenie warstwy danych od logiki, aby uniknąć eksponowania struktury bazy.
- **Exception Handling (`exception/`)**: Globalne przechwytywanie błędów (`ControllerAdvice`), poprawne kody statusu i zwracanie JSONa w przypadku problemów (np. błąd walidacji, nie znaleziono zasobu).
- **Events (`event/`)**: Użycie mechanizmu `ApplicationEventPublisher` (Spring Events) wysyła powiadomienie po dodaniu nowego kierowcy, obsługa asynchroniczna.

## Bezpieczeństwo i Baza
- **Security:** API jest chronione za pomocą Basic Authentication.
- **Baza danych:** W pamięci, z użyciem bazy H2. 
- **Walidacja danych:** Punktacja nie może być mniejsza niż 0, weryfikacja unikatowości numerów kierowców i niepuste nazwy.

## Uruchomienie projektu

1. **Wymagania:** 
   - Środowisko Java 21+ 
   - Maven

2. **Kompilacja i uruchomienie:**
   ```bash
   ./mvnw spring-boot:run
   ```
   *Ewentualnie zaimportuj projekt w IntelliJ / Eclipse i uruchom główną klasę `F1ApiApplication`.*

3. **Dostęp do API:**
   Aplikacja będzie działać na porcie: `8080` (domyślnym dla Spring Boot).

   **Dane logowania (Basic Auth):**
   - Username: `admin`
   - Password: `admin`

4. **Dostęp do H2 (Konsola do pglądania danych):**
   - URL: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:f1db`
   - User: `sa`
   - Pass: `password`

## Endpoints

(Wymagają nagłówka `Authorization: Basic YWRtaW46YWRtaW4=`)

| Metoda | Endpoint | Opis |
| --- | --- | --- |
| `GET` | `/api/drivers` | Pobiera listę wszystkich kierowców |
| `GET` | `/api/drivers/{id}` | Pobiera dane konkretnego kierowcy |
| `POST` | `/api/drivers` | Dodaje nowego kierowcy (Body w JSON wymagane) |
| `PUT` | `/api/drivers/{id}` | Aktualizuje dane kierowcy by ID |
| `DELETE` | `/api/drivers/{id}` | Usuwa kierowcę |

### Przykładowy Payload `POST /api/drivers`
```json
{
  "firstName": "Max",
  "lastName": "Verstappen",
  "carNumber": 1,
  "points": 340,
  "team": "Red Bull Racing"
}
```

## Uruchomienie Testów
Aplikacja zawiera przykładowe testy jednostkowe (`DriverServiceTest`). Zbudowano je za pomocą JUnit5 oraz Mockito.

```bash
./mvnw test
```
