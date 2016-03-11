var app = angular.module('app', ['ngTouch']);


app.controller('MainCtrl', function ($scope, $http) {

  $scope.addCustomerName = ""
  $scope.addTechName = ""
  $scope.currentDate = new Date();
  $scope.defaultTable = false;
  $scope.monthTable = false;
  $scope.oldMonth;
  var newMonth = false;

  var monthNames = ["January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
  ];

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
    var newDate = new Date(date) ;
    newDate.setDate(date.getDate() + 7);
    if (date.getMonth() != newDate.getMonth()){
      newMonth = true;
      $scope.oldMonth = monthNames[date.getMonth()];
    }
    return newDate;
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
      case "RESET_ALL" :
        url = $scope.base + "resetAll"
          $scope.currentDate = new Date();
        break;
    }

    $http.get(url)
    .then(function(response) {
      hideTables();
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
      var invoiceArray = [];
      var tempArray = [];
      $scope.keyZ = Object.keys(response.data[0]);
      for ( var i = 0; i < data.length; i++ ) {
        if (currentTechId == data[i].technician.idTechnician) {
          tempArray.push(data[i])
        } else {
          invoiceArray.push(tempArray);
          currentTechId = data[i].technician.idTechnician
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
    if (newMonth) processNewMonth();
  }

  var hideTables = function() {
    $scope.defaultTable = false;
    $scope.forceWeekTable = false;
    $scope.monthTable = false;
  }

  var processNewMonth = function() {
    var url = $scope.base + $scope.toJavaDate($scope.currentDate) + "/listMonthlyInvoice"
    $http.get(url)
    .then(function(response) {
      console.log(response.data.length)
      console.log(response);
        var monthData = response.data;
        if (monthData[0]) {
          var currentCustomerId = monthData[0].customer.idCustomer;
          var invoiceArray = [];
          var tempArray = [];
          $scope.monthKeyZ = Object.keys(monthData[0]);
          for (var i = 0; i < monthData.length; i++) {
            if (currentCustomerId == monthData[i].customer.idCustomer) {
              tempArray.push(monthData[i])
            } else {
              invoiceArray.push(tempArray);
              currentCustomerId = monthData[i].customer.idCustomer
              tempArray = [];
              tempArray.push(monthData[i])
            }
          }
          invoiceArray.push(tempArray);
          $scope.processNewMonthData = invoiceArray;
          $scope.monthTable = true;
        } else {
          $scope.monthTable = false;
        }
      })
    newMonth = false;
  }

});