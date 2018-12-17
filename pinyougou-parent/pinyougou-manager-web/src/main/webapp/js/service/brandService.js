app.service("brandService",function ($http) {

    this.findAll = function () {
        return $http.post("../brand/findAll.do");
    };

    this.findPage = function (pageNum,pageSize) {
        return $http.get("../brand/findPage.do?pageNum="+pageNum+"&pageSize="+pageSize);
    };

    this.findOne = function (id) {
        return $http.get("../brand/findById.do?id="+id);
    };

    this.save = function (newBrand) {

        return $http.post("../brand/save.do",newBrand);
    };

    this.update = function (newBrand) {
        return $http.post("../brand/update.do",newBrand);
    };

    this.dele = function (checkIds) {
        return $http.get("../brand/deleteByIds.do?ids="+checkIds);
    };

    this.search = function (pageNum, pageSize,searchBrand) {
        return $http.post("../brand/findByCondition.do?pageNum="+pageNum+"&pageSize="+pageSize,searchBrand);
    }

    this.selectOptionList = function () {
        return $http.get("../brand/selectOptionList.do");
    }

});