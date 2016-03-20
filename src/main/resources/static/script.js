var app = angular.module('app', ['ngTouch', 'ngRoute']);

app.config(function($routeProvider) {
  $routeProvider
  .when('/', {
    templateUrl : 'pages/home.html',
    controller : 'MainCtrl'
  })
  .when('/manualReports', {
    templateUrl : 'pages/manualReports.html',
    controller : 'manualReportsCtrl'
  });
});


app.controller('MainCtrl', function ($scope, $http) {

  $scope.addCustomerName = ""
  $scope.addTechName = ""
  $scope.currentDate = new Date();
  $scope.defaultTable = false;
  $scope.monthTable = false;
  $scope.oldMonth;
  var newMonth = false;
  $scope.message = "Testing"

  var monthNames = ["January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
  ];

  $scope.dateNumbers = ['1st'];
  for ( var i = 2; i < 32; i++) {
    $scope.dateNumbers.push(i);
  }

  $scope.years = ['2016', '2017', '2018', '2019', '2020'];

  $scope.months = monthNames;

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
    console.log("sending")
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

  $scope.parseInvoice = function (i, k, j) {
    if (k == "dateOfService") {
      var sendDate = new Date(j);
      return "" + monthNames[sendDate.getMonth()] + " " + 
      sendDate.getDate() + ", " + 
      sendDate.getFullYear();
    } else if (k == "customer") {
      return j["name"];
    } else if (k == "technician") {
      return j["name"];
    }
    return j;
  }

  $scope.parseListData = function (i, k, j) {
    if (k == "signUpDate" ||
      k == "nextDayOfService" ||
      k == "dateOfService") {
      var sendDate = new Date(j);
    return "" + monthNames[sendDate.getMonth()] + " " + 
    sendDate.getDate() + ", " + 
    sendDate.getFullYear();
  } else if (k == "customer") {
    return j["name"];
  } else if (k == "technician") {
    return j["name"];
  }
  return j;
}

$scope.debugLog = function (a, b, c) {
  console.log(a);
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

app.controller('manualReportsCtrl', function ($scope, $http) {
  $scope.defaultReportTable;

  $scope.message2 = "???"
  $scope.firstTypeBox = "Monthly Report";
  $scope.monthOne = "January";
  $scope.dayOne = "1st"; 
  $scope.yearOne = "2016";
  $scope.monthTwo = "January";
  $scope.dayTwo = "1st";
  $scope.yearTwo = "2016";

  $scope.isDayOneDisabled = function() {
    return $scope.firstTypeBox == "Monthly Report"
  }
  $scope.isDayTwoDisabled = function() {
    return $scope.firstTypeBox == "Monthly Report"
  }
  $scope.isMonthOneDisabled = function() {
  }
  $scope.isMonthTwoDisabled = function() {
  }
  $scope.isYearOneDisabled = function() {
  }
  $scope.isYearTwoDisabled = function() {
  }

  $scope.sendReportRequest = function() {
    console.log("sendingRR")
    var url = $scope.base;
    var type = $scope.firstTypeBox
    var monthOne = "";
    var dayOne = $scope.dayOne == '1st' ? '1' : $scope.dayOne;
    var yearOne = $scope.yearOne
    var monthTwo = "";
    var dayTwo = $scope.dayTwo == '1st' ? '1' : $scope.dayTwo;
    var yearTwo = $scope.yearTwo

    for (var i = 0; i < $scope.months.length; i++) {
      if ($scope.monthOne == $scope.months[i]) {
        monthOne = i;
        break;
      }
    }
    for (var i = 0; i < $scope.months.length; i++) {
      if ($scope.monthTwo == $scope.months[i]) {
        monthTwo = i;
        break;
      }
    }

    var postMonthData = {
      type : type,
      dayOne : dayOne,
      monthOne : monthOne,
      yearOne : yearOne,
      dayTwo : dayTwo,
      monthTwo : monthTwo,
      yearTwo : yearTwo
    }

    switch(type) {
      case "Monthly Report" :
        url = $scope.base + "monthWithPost"
        break;
      case "Monthly ReportTemp" :
        url = $scope.base + monthOne + "/" + yearOne + "/" + monthTwo + "/" + yearTwo + "/generateMonth"
        break;
    }

    $http.post(url, postMonthData)
      .then(function (response) {
        hideTables();
        console.log(response);
        switch(type) {
          default:
          processMonthlyReport(response);
          break;
        }
      })


    /*
    $http.get(url)
    .then(function(response) {
      hideTables();
      console.log(response);
      switch(type) {
        default : 
        processMonthlyReport(response);
        break;
      }
    })
    */
  }

  var processMonthlyReport = function(response) {
    var reportData = response.data;
    if (reportData[0]) {
      var currentCustomerId = reportData[0].customer.idCustomer;
      var invoiceArray = [];
      var tempArray = [];
      $scope.reportKeyZ = Object.keys(reportData[0]);
      for (var i = 0; i < reportData.length; i++) {
        if (currentCustomerId == reportData[i].customer.idCustomer) {
          tempArray.push(reportData[i])
        } else {
          invoiceArray.push(tempArray);
          currentCustomerId = reportData[i].customer.idCustomer
          tempArray = [];
          tempArray.push(reportData[i])
        }
      }
      invoiceArray.push(tempArray);
      $scope.processedMonthlyReportData = invoiceArray
      $scope.defaultReportTable = true;
    } else {
      $scope.defaultReportTable = false;
    }
  }

  var hideTables = function() {
    $scope.defaultReportTable = false;
  }

});

app.controller('HeaderCtrl', function ($scope, $location) {

  $scope.isActivePage = function (viewLocation) { 
    return viewLocation === $location.path();
  };

  $scope.returnView = function(viewLocation) {
    console.log(viewLocation);
    console.log($location.path())
  }
  });