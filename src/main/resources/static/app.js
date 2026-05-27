var app = angular.module('f1App', []);

app.controller('DriverController', function($scope, $http) {

    $scope.drivers = [];
    $scope.errorMessage = '';

    // Setup Basic Auth header for all requests
    var authHeader = 'Basic ' + btoa('admin:admin');
    $http.defaults.headers.common['Authorization'] = authHeader;

    var apiUrl = '/api/drivers';

    $scope.loadDrivers = function() {
        $scope.errorMessage = '';
        $http.get(apiUrl)
            .then(function(response) {
                $scope.drivers = response.data;
            }, function(error) {
                $scope.errorMessage = 'Błąd pobierania danych: ' + (error.data.message || error.statusText);
            });
    };

    $scope.addFakeDriver = function() {
        var randomNum = Math.floor(Math.random() * 99) + 2;
        var newDriver = {
            firstName: "Test",
            lastName: "Driver " + randomNum,
            carNumber: randomNum,
            points: 0,
            team: "Test Team"
        };

        $http.post(apiUrl, newDriver)
            .then(function(response) {
                $scope.loadDrivers();
                console.log("Dodano kierowcę!");
            }, function(error) {
                $scope.errorMessage = 'Błąd dodawania: ' + (error.data.message || error.statusText);
            });
    };

    $scope.deleteDriver = function(id) {
        $http.delete(apiUrl + '/' + id)
            .then(function(response) {
                $scope.loadDrivers();
            }, function(error) {
                $scope.errorMessage = 'Błąd usuwania: ' + (error.data.message || error.statusText);
            });
    }

    // Auto-load on init
    $scope.loadDrivers();
});

