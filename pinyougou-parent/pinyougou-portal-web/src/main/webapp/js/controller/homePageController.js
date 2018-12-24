app.controller("homePageController",function ($scope,homePageService) {

    //广告数据
    $scope.advertisementList = [];

    //获取轮播图
    $scope.findCarouselById = function (id) {
        homePageService.findCarouselById(id).success(
            function (response) {
                $scope.advertisementList[id] = response;
            }
        )
    }

    //跳转到查询页面
    $scope.skipToSearch = function (keywords) {
        location.href = "http://localhost:9104/search.html#?keywords="+keywords;
    }

})