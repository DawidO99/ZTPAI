# F1 Paddock API

## Temat projektu
System zarządzania danymi o kierowcach Formuły 1.
Aplikacja stanowi w pełni funkcjonalne zaplecze (REST API) dla portalu sportowego lub bazy danych FIA. Pozwala ona administratorom na kompleksowe zarządzanie uczestnikami rywalizacji – dodawanie nowych kierowców, aktualizację ich punktacji, przypisywanie do zespołów oraz monitorowanie klasyfikacji zespołowej. Aplikacja zawiera inicjalnie dane odzwierciedlające oficjalny rozkład sił z sezonu 2026.

## Linki
- **Repozytorium GitHub:** [Wstaw Link do Repozytorium]()
- **Demo Video:** [Wstaw Link do Video]()

## Architektura i Pakiety
Projekt został napisany z wykorzystaniem framweorku Spring Boot (Java 21) oraz Angular, zgodnie z dobrymi praktykami wielowarstwowej architektury (Multilayer Architecture). Posiada podział na:
- **`controller/`**: Klasy odbierające żądania HTTP (REST). Konwersja protokołu na wywołania serwisów.
- **`service/`**: Warstwa abstrakcji przechowująca reguły biznesowe aplikacji (np. walidacja maksymalnej liczby kierowców w zespole, symulacja punktów).
- **`repository/`**: Warstwa dostępu do danych wykorzystująca Spring Data JPA z customowymi metodami zapytań (np. `findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase`).
- **`dto/` oraz `mapper/`**: Wzorzec Data Transfer Object izolujący strukturę bazy danych od odpowiedzi zwracanych na zewnątrz.
- **`exception/`**: Globalna logika obsługi błędów (`@ControllerAdvice`), tłumacząca logikę wyjątków Javy na zrozumiałe dla użytkownika obiekty i statusy HTTP w JSON.
- **`event/`**: Implementacja Spring Application Events (Pub/Sub) automatyzująca tworzenie wpisów w logach przy dodawaniu nowego rekordu.

## Główne Funkcjonalności
- **Pełen interfejs CRUD**: Manipulowanie bazą zawodników poparte obostrzeniami wymaganymi przez FIA (max 2 głównych kierowców na stajnię wyścigową, unikatowe numery startowe pojazdów).
- **Klasyfikacja Mistrzostw Świata (Constructors' and Drivers' Championship)**: Grupowanie i sortowanie danych metodami strumieniowymi z wyłonieniem statystyk (aktualizujących się bez przeładowania całej strony).
- **Live Search & Server-side Sorting**: Obsługa wyszukiwania wzorców na żywo z poziomu aplikacji klienckiej wraz z zapytaniami zdejmującymi ciężar sortowania na bazę danych.
- **Złożona Symulacja Grand Prix (`/simulate-race`)**: Funkcja opierająca się na generatorze wartości pseudolosowych i relatywnej przydrożnej zmiennej `overall` (rating siły danego kierowcy w skali 0-99). System bierze pod uwagę rating bazy by realistycznie, ale z nutką błędu losowości rozdać realne punkty wyścigowe (od 25 za pierwsze miejsce po 1) za 24 rundy GP w danym kalendarzu F1.
- **Generowanie Raportów Automatycznych**: Pobieranie do zmapowanego pliku o popularnym rozszerzeniu strukturalnym (*.csv) na dysk komputera bezpośrednio ze strumienia danych odpowiedzi `ResponseEntity<byte[]>`.

## Bezpieczeństwo i Baza Danych
- **Uwierzytelnienie:** REST API w całości objęte zasłoną filtrowania HTTP Spring Security za sprawą metody **Basic Authentication**. Klient webowy realizuje je podpinając zakodowane w base64 autoryzacje `X-Requested-With: XMLHttpRequest` pod wbudowane skrypty kontrolera. 
  - Domyślny użytkownik logowania: `admin` 
  - Domyślne hasło testowe: `admin`
- **Baza danych:** Szybka, relacyjna baza testowa ładująca się bezpośrednio w pamięci aplikacji (`H2 Database`). Wbudowany skrypt klasy `CommandLineRunner` odtwarza wymagany stan domyślny, bez konieczności migracji fizycznego serwera SQL w celu przetestowania środowiska.

## Uruchomienie projektu

1. **Wymagania Systemowe:** 
   - Konfigurator ścieżki i zainstalowana Java Development Kit (JDK 21+)
   - Pobieracz pakietów Apache Maven

2. **Kompilacja i uruchomienie (Terminal / Linux / MacOS):**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Dostęp do Aplikacji:**
   Aplikacja startuje domyślnie na wbudowanym kontenerze serwera Tomcat. Dostęp pod adresem:
   - **Frontend GUI:** `http://localhost:8080/`
   - **Baza H2 Konsola:** `http://localhost:8080/h2-console` (W systemie plikowym properties JDBC podano link domyślny `jdbc:h2:mem:f1db`, user `sa`, pass `password`)

## Specyfikacja Endpointów (API w formacie JSON)
Każdy call obwarowany jest z nagłówkiem `Authorization: Basic YWRtaW46YWRtaW4=`.

| Metoda | Ścieżka URL | Opis Aktywności |
| --- | --- | --- |
| `GET` | `/api/drivers` | Zwraca posortowaną i/lub przefiltrowaną listę kierowców (Parametry query: `search`, `sortBy`, `dir`) |
| `GET` | `/api/drivers/{id}` | Pobiera i zwraca precyzyjnie obiekt DTO jednego wyszczególnionego ID. Rzuca 404 Not Found w przypadku błędu. |
| `GET` | `/api/drivers/standings` | Automatycznie strumieniuje wartości, grupując je po kluczu zespołu rzucając finalnie ich zagregowaną sumę do tabel. |
| `POST` | `/api/drivers` | Tworzy i wdraża obostrzenia z `@Valid`. Zwraca `HTTP 201 Created` po sukcesie. |
| `PUT` | `/api/drivers/{id}` | Formatuje dany unikat i podmienia jego wariant ujednolicając do standardu struktury encyjnej. |
| `DELETE` | `/api/drivers/{id}` | Permanentnie usuwa wybrany rekord z H2 za sprawą metody w Spring Data Repository. |
| `POST` | `/api/drivers/simulate-race` | Uruchamia algorytm szuflowania zmiennych siły z podsystemem kalendarza wyścigów (zwraca String[] zwycięzców i powiększa pulę zsumowanych punktacji). |
| `POST` | `/api/drivers/reset` | Cofnij licznik wyścigów kalendarza Grand Prix na 0 i wymusza redukcję zmiennych `.setPoints(0)` u wszystkich zmapowanych kierowców. |
| `GET` | `/api/drivers/season-status` | Udostępnia proste wartości w słowniku Map określające fazę rozegranych turniejów. |
| `GET` | `/api/drivers/export` | Zwraca strumień binarny formujący się w wymuszającym rozszerzeniu po stronie klienckiej jako raport `.csv` do pobrania. |

### Przykładowy Payload `POST /api/drivers`
```json
{
  "firstName": "Liam",
  "lastName": "Lawson",
  "carNumber": 30,
  "points": 14,
  "overall": 80,
  "team": "Racing Bulls"
}
```

## Opis wdrożonych Testów
Aplikacja została pomyślnie zhermetyzowana i przetestowana jednostkowo w obrębie szkieletu `JUnit5` oraz techniki `Mockito`. Skonstruowane testy pokrywają główną logikę biznesową zadeklarowaną w `DriverService`, taką jak:
- Walidacja zachowania przy próbie zduplikowania numeru samochodu (`carNumber`).
- Potwierdzenie poprawnego zresetowania sezonu (`resetSeason()`) ze wszystkich zgromadzonych punktów w każdym obiekcie bazy.
- Sprawdzenie bezproblemowego podnoszenia obiektów lub zgłaszania pożądanego w formacie wyjątku typu `ResourceNotFoundException`.

Aby zweryfikować integralność testowanego kodu i wyeliminować ewentualne przyszłe regresje po modyfikacjach, uruchom:
```bash
./mvnw test
```
