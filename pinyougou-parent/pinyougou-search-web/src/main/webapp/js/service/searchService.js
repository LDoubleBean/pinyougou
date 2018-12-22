app.service('searchService',function ($http) {

    this.searchItem = function (entity) {
        return $http.post('search/searchItem.do',entity);
    };

})