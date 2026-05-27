var app = angular.module('f1App', []);

app.controller('DriverController', function($scope, $http) {

    $scope.drivers = [];
    $scope.standings = [];
    $scope.lastRaceResults = [];
    $scope.errorMessage = '';
    $scope.searchQuery = '';

    $scope.teams = [
        "Red Bull Racing", "Mercedes", "Ferrari", "McLaren", "Aston Martin",
        "Alpine", "Williams", "RB", "Sauber", "Haas"
    ];

    $scope.newDriver = {
        firstName: '',
        lastName: '',
        carNumber: null,
        points: 0,
        team: $scope.teams[0] // Default team
    };

    $scope.editingDriver = null;

    $scope.sortField = 'id';
    $scope.sortDir = 'asc';
    $scope.sortIcon = ' 🔼';

    $scope.sortBy = function(field) {
        if ($scope.sortField === field) {
            $scope.sortDir = $scope.sortDir === 'asc' ? 'desc' : 'asc';
            $scope.sortIcon = $scope.sortDir === 'asc' ? ' 🔼' : ' 🔽';
        } else {
            $scope.sortField = field;
            $scope.sortDir = 'asc';
            $scope.sortIcon = ' 🔼';
        }
        $scope.loadDrivers();
    };

    // Setup Basic Auth header for all requests
    var authHeader = 'Basic ' + btoa('admin:admin');
    $http.defaults.headers.common['Authorization'] = authHeader;

    var apiUrl = '/api/drivers';

    $scope.loadStandings = function() {
        $http.get(apiUrl + '/standings')
            .then(function(response) {
                $scope.standings = response.data;
            });
    };

    $scope.loadDrivers = function() {
        $scope.errorMessage = '';
        var url = apiUrl + "?sortBy=" + $scope.sortField + "&dir=" + $scope.sortDir;
        if ($scope.searchQuery) {
            url += "&search=" + encodeURIComponent($scope.searchQuery);
        }

        $http.get(url)
            .then(function(response) {
                // sort is now done on server
                $scope.drivers = response.data;
            }, function(error) {
                $scope.errorMessage = 'Błąd pobierania danych: ' + (error.data.message || error.statusText);
            });
    };

    $scope.exportCsv = function() {
        window.location.href = apiUrl + '/export';
    };

    $scope.simulateRace = function() {
        if(confirm("🚥 Czy chcesz wylosować wyniki nowego GP i przydzielić punkty kierowcom?")) {
            $http.post(apiUrl + '/simulate-race')
                .then(function(response) {
                    $scope.lastRaceResults = response.data;
                    $scope.loadDrivers();
                    $scope.loadStandings();
                }, function(error) {
                    $scope.errorMessage = 'Błąd symulacji wyścigu: ' + (error.data.message || error.statusText);
                });
        }
    };

    $scope.submitForm = function() {
        if ($scope.editingDriver) {
            // Update
            $http.put(apiUrl + '/' + $scope.newDriver.id, $scope.newDriver)
                .then(function(response) {
                    $scope.loadDrivers();
                    $scope.loadStandings();
                    $scope.resetForm();
                }, function(error) {
                    $scope.errorMessage = 'Błąd aktualizacji: ' + (error.data.message || error.statusText);
                });
        } else {
            // Create
            $http.post(apiUrl, $scope.newDriver)
                .then(function(response) {
                    $scope.loadDrivers();
                    $scope.loadStandings();
                    $scope.resetForm();
                }, function(error) {
                    $scope.errorMessage = 'Błąd dodawania: ' + (error.data.message || error.statusText);
                });
        }
    };

    $scope.editDriver = function(driver) {
        $scope.editingDriver = true;
        // Copy data to form
        $scope.newDriver = angular.copy(driver);
    };

    $scope.resetForm = function() {
        $scope.editingDriver = false;
        $scope.newDriver = {
            firstName: '',
            lastName: '',
            carNumber: null,
            points: 0,
            team: $scope.teams[0]
        };
        $scope.errorMessage = '';
    };

    $scope.deleteDriver = function(id) {
        if(confirm("Czy na pewno chcesz usunąć kierowcę?")) {
            $http.delete(apiUrl + '/' + id)
                .then(function(response) {
                    $scope.loadDrivers();
                    $scope.loadStandings();
                }, function(error) {
                    $scope.errorMessage = 'Błąd usuwania: ' + (error.data.message || error.statusText);
                });
        }
    }

    // Auto-load on init
    $scope.loadDrivers();
    $scope.loadStandings();
});
