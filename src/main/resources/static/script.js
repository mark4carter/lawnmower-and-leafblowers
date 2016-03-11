var app = angular.module('app', ['ngTouch']);


app.controller('MainCtrl', function ($scope, $http) {

  $scope.$watch('search', function() {
    //sendit();
  });
  $scope.$watch('selectchoice', function() {
    //sendit();
  });
  $scope.addCustomerName = ""
  $scope.addTechName = ""
  $scope.currentDate = new Date();
  $scope.defaultTable = false;

  $scope.selectchoice =" "
  $scope.base = "http://localhost:8080/controller/"
  $scope.getData = function(item, key) {
    return item[key];
  }

  $scope.toJavaDate = function(dateInMilli) {
    var date = new Date(dateInMilli);
    var clientMilliseconds = date.getTime();
    return clientMilliseconds;
  }

  $scope.addWeek = function(date) {
    return date.setDate(date.getDate() + 7);
  }

  $scope.sendit = function(action) {
    console.log("sending");
    var url = $scope.base + "/listCustomers/";
    switch(action) {
      case "ADD_CUSTOMER" :
        url = $scope.base + $scope.addCustomerName + "/" + $scope.toJavaDate($scope.currentDate) + "/addCustomer"
        $scope.addCustomerName = ""
        break;
      case "ADD_TECH" :
        url = $scope.base + $scope.addTechName + "/addTechnician"
        $scope.addTechName = ""
        break;
      case "LIST_TECHNICIANS" :
        url = $scope.base + "listTechnicians"
        break;
      case "LIST_CUSTOMER" :
        url = $scope.base + "listCustomers"
        break;
      case "LIST_INVOICES" :
        url = $scope.base + "listInvoices"
        break;
      case "FORCE_NEXT_WEEK" :
        $scope.currentDate = $scope.addWeek($scope.currentDate);
        url = $scope.base + $scope.toJavaDate($scope.currentDate) + "/forceNextWeek"
        $scope.currentDate = new Date($scope.currentDate)
        break;
    }

    $http.get(url)
    .then(function(response) {
      console.log(response);
      switch(action) {
        case "FORCE_NEXT_WEEK" :
          $scope.defaultTable = false;
          processForceNextWeek(response);
          break;
        default : 
          $scope.myData = response.data;
          if (response.data[0]) {
            $scope.defaultTable = true;
            $scope.keyZ = Object.keys(response.data[0]);
          } else {
            $scope.defaultTable = false;
          }
          break;
      }
    })
  }

  var processForceNextWeek = function(response) {
    var data = response.data;
    if (data[0]) {
      var currentTechId = data[0].technician.idTechnician;
      var currentTechIndex = 0;
      var invoiceArray = [];
      var tempArray = [];
      $scope.keyZ = Object.keys(response.data[0]);
      for ( var i = 0; i < data.length; i++ ) {
        if (currentTechId == data[i].technician.idTechnician) {
          tempArray.push(data[i])
        } else {
          invoiceArray.push(tempArray);
          currentTechIndex++;
          tempArray = [];
          tempArray.push(data[i])
        }
      }
      invoiceArray.push(tempArray);
      $scope.forceNextWeekData = invoiceArray;
      $scope.forceWeekTable = true;
    } else {
      $scope.forceWeekTable = false;
    }
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