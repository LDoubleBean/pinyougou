app.service("homePageService",function ($http) {

    //根据id查询
    this.findCarouselById = function (id) {
        return $http.get('/homePage/findCarouselById.do?id='+id);
    }

})