var app = angular.module('app', ['ngTouch']);


app.controller('MainCtrl', function ($scope, $http) {

  $scope.$watch('search', function() {
    sendit();
  });
  $scope.$watch('selectchoice', function() {
    sendit();
  });
  $scope.search = ""

  $scope.selectchoice =" "
  $scope.base = "http://localhost:8080"
  $scope.getData = function(item, key) {
    return item[key];
  }

  $scope.sendit = function() {
    console.log("sending");
    var url = "controller/";
    switch($scope.selectchoice) {
      case "LIST_PCS" :
        url = $scope.base + "/listPCs"
        break;
      case "LIST_GOALS" :
        url = $scope.base + $scope.search + "/listGoals"
        break;
    }

    $http.get($scope.base + url)
    .then(function(response) {
      console.log(response);
      $scope.myData = response.data;
      $scope.keyZ = Object.keys(response.data[0]);
    })
  }
});







app.controller('SecondaryCtrl', function ($scope, $http) {

  $scope.search = ""
  $scope.selectchoice ="LIST_PCS"
  $scope.base = "http://localhost:8080/api/game/"

  $scope.sendit = function() {
    console.log("sending?")
    var url = "";
      switch($scope.selectchoice) {
        case "CREATE_PLAYER" :
          url = $scope.base + $scope.search + "/createPlayerCharacter"
          break;
        case "CREATE_LOCATION" :
          url = $scope.base + $scope.search + "/createLocation"
          break;
        case "CREATE_THREAD" :
          url = $scope.base + $scope.search + "/createThread"
          break;
        case "ADD_PLAYER_TO_THREAD" :
          url = $scope.base + $scope.search + "/" + $scope.arg2 + "/addPlayerToThread"
          break;
        case "DELETE_BY_PLAYERID" :
          url = $scope.base + $scope.search + "/RemoveByPlayerId"
          httpDelete(url);
          url = "";
          break;
        default :
          fetch()
          break;
      }

    if (url) {
      $http.post(url)
        .then(function(response) {
          console.log(response);
          $scope.myData = response.data;
          $scope.keyZ = Object.keys(response.data[0]);
        })
    }
  }

  function httpDelete(url) {
    $http.delete(url)
      .then(function(response) {
        console.log(response);
        $scope.myData = response.data;
        $scope.keyZ = Object.keys(response.data[0]);
      })
    }

  function fetch() {
    var url = "";
    switch($scope.selectchoice) {
      case "LIST_PCS" :
        url = $scope.base + "/listPCs"
        break;
      case "LIST_GOALS" :
        url = $scope.base + $scope.search + "/listGoals"
        break;
      case "PLAYER_BY_LOCATION" :
        url = $scope.base + $scope.search + "/playerByLocationId"
        break;
      case "LIST_PCS_BY_GOAL" :
        url = $scope.base + $scope.search + "/listPlayerByGoal"
        break;
      case "LIST_ALL_GOALS" :
        url = $scope.base + "/listThreadGoals"
        break;
    }

    $http.get(url)
      .then(function(response) {
        console.log(response);
        $scope.myData = response.data;

        $scope.keyZ = Object.keys(response.data[0]);
      })
    }
});