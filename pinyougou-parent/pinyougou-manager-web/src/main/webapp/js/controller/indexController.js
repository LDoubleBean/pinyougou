app.controller("indexController",function ($scope,indexService) {

    $scope.getName = function () {
        indexService.getLoginName().success(function (response) {
            $scope.loginName = response.loginName;
        })
    }

});