app.controller('searchController',function ($scope, searchService) {
    
    $scope.searchTerm = {"keywords":"","brand":"","category":"","price":"","pageNum":1,"pageSize":40,"spec":{}};
    
    //搜索商品
    $scope.searchItem = function () {
        $scope.searchTerm.pageNum = parseInt( $scope.searchTerm.pageNum);
        searchService.searchItem($scope.searchTerm).success(
            function (response) {
                $scope.searchResult = response;
                $scope.getPageColumn();
            }
        );
    }

    //添加用户选着项
    $scope.addSelect = function (key,keyValue) {

        if (key == "brand") {
            $scope.searchTerm.brand = keyValue;
        } else if (key == "category") {
            $scope.searchTerm.category = keyValue;
        } else if (key == "price") {

            $scope.searchTerm.price = keyValue ;
        } else {
            $scope.searchTerm.spec[key] = keyValue;
        }
    }
    
    //删除用户选择项
    $scope.deleSelect = function (key) {

        if (key == "brand") {
            $scope.searchTerm.brand = '';
        } else if (key == "category") {
            $scope.searchTerm.category = '';
        } else if (key == "price") {
            $scope.searchTerm.price = '';
        } else {
            delete $scope.searchTerm.spec[key];
        }
    }

    //获取地址栏
    $scope.getPageColumn = function () {
        $scope.pageColumn = [];
        var pageNum = $scope.searchTerm.pageNum;
        var pageTotal = $scope.searchResult.pageTotal;
        var firstPage = 1;
        $scope.firstPoint = true;
        $scope.lastPoint = true;

        if ( pageTotal > 5) {
            if (pageNum <= 3) {
                pageTotal = 5;
                $scope.firstPoint = false;
            } else if (pageNum >= (pageTotal - 2)) {
                firstPage = pageTotal - 4;
                $scope.lastPoint = false;
            } else {
                firstPage = $scope.searchTerm.pageNum - 2;
                pageTotal = $scope.searchTerm.pageNum + 2;
            }
        } else {
            $scope.firstPoint = false;
            $scope.lastPoint = false;
        }

        for (var i = firstPage; i <= pageTotal; i++) {
            $scope.pageColumn.push(i);
        }

    }

    //上一页
    $scope.prePage = function () {
        if ($scope.searchTerm.pageNum - 1 > 0) {
            $scope.searchTerm.pageNum = $scope.searchTerm.pageNum - 1;
            $scope.searchItem();
        }
    }

    //上一页
    $scope.nextPage = function () {
        if ($scope.searchTerm.pageNum + 1 < $scope.searchResult.pageTotal) {
            $scope.searchTerm.pageNum = $scope.searchTerm.pageNum + 1;
            $scope.searchItem();
        }
    }

    //显示点击的页面
    $scope.showPage = function (pageNum) {
        $scope.searchTerm.pageNum = pageNum;
        $scope.searchItem();
    }

    //直接搜索单个品牌时，隐藏品牌列表
    $scope.hideBrand = function () {
        for (var i = 0; i < $scope.searchResult.brandList.length; i++) {
            if ($scope.searchTerm.keywords ==  $scope.searchResult.brandList[i].text) {
                return true;
            }
        }
        return false;
    }

})