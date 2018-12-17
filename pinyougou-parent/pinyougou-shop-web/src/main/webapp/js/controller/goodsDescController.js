//控制层
app.controller('goodsDescController', function ($scope, $controller, $location, goodsDescService, uploadFileService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsDescService.findAll().success(
            function (response) {
                $scope.list = response;

            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsDescService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var goodsid = $location.search()['id'];//获取参数
        if (goodsid == null) {
            return;
        }
        goodsDescService.findOne(goodsid).success(
            function (response) {
                $scope.entity = response;
                //富文本显示
                editor.html($scope.entity.tbGoodsDesc.introduction);
                //照片显示
                $scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);
                //扩展属性
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
                //规格
                $scope.entity.tbGoodsDesc.specificationItems = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
                //sku列表
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.tbGoods.id != null) {//如果有ID
            serviceObject = goodsDescService.update($scope.entity); //修改
        } else {
            serviceObject = goodsDescService.add($scope.entity);//增加
        }
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        serviceObject.success(
            function (response) {
                if (response.success) {
                    alert(response.message);
                    location.href = "goods.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsDescService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsDescService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //上传文件
    $scope.uploadFile = function () {
        uploadFileService.uploadFile().success(function (response) {
            if (response.success) {
                $scope.imageFile.url = response.message;
            } else {
                alert(response.message);
            }
        })
    }

    //添加图片列表
    $scope.entity = {tbGoods: {}, tbGoodsDesc: {itemImages: [], specificationItems: []}};
    $scope.addImageList = function (pojo) {
        $scope.entity.tbGoodsDesc.itemImages.push(pojo);
    }

    //删除图片列表
    $scope.deleImageList = function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index, 1);
    }


    //获取一级分类列表
    $scope.selectCategory1Id = function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.category1IdList = response;
        })
    }

    //获取二级分类列表
    $scope.$watch('entity.tbGoods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.category2IdList = response;
        })
    })

    //获取三级分类列表
    $scope.$watch('entity.tbGoods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.category3IdList = response;
        })
    })

    //获取模板ID
    $scope.$watch('entity.tbGoods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.tbGoods.typeTemplateId = response.typeId;
        })
    })

    //获取模板列表
    $scope.$watch('entity.tbGoods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
            $scope.typeTemplate = response;
            $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
            if ($location.search()['id'] == null) {
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
            }
        })

        typeTemplateService.findTemplateAndSpecOption(newValue).success(function (response) {
            $scope.specList = response;
        })

    })

    //添加选中的规格
    $scope.addOption = function (event, specName, optionName) {
        var list = $scope.searchOption($scope.entity.tbGoodsDesc.specificationItems, "attributeName", specName);
        if (list !== null) {
            if (event.target.checked) {
                list.attributeValue.push(optionName);
            } else {
                var index = list.attributeValue.indexOf(optionName);
                list.attributeValue.splice(index, 1);
                if (list.attributeValue.length == 0) {
                    var index2 = $scope.entity.tbGoodsDesc.specificationItems.indexOf(list);
                    $scope.entity.tbGoodsDesc.specificationItems.splice(index2, 1);
                }
            }
        } else {
            $scope.entity.tbGoodsDesc.specificationItems.push({
                "attributeName": specName,
                "attributeValue": [optionName]
            });
        }
    }

    //显示选取结果组合列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}];
        for (var i = 0; i < $scope.entity.tbGoodsDesc.specificationItems.length; i++) {
            $scope.entity.itemList = addItemToList($scope.entity.itemList, $scope.entity.tbGoodsDesc.specificationItems[i].attributeName, $scope.entity.tbGoodsDesc.specificationItems[i].attributeValue)
        }
    }
    //添加选项列到结果集
    addItemToList = function (list, attributeName, attributeValues) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < attributeValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));
                newRow.spec[attributeName] = attributeValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

    //回显示被选中的值
    $scope.getSelectOption = function (specName, OptionName) {
        var list = $scope.searchOption($scope.entity.tbGoodsDesc.specificationItems, "attributeName", specName);
        if (list != null) {
            for (var i = 0; i < list.attributeValue.length; i++) {
                if (list.attributeValue[i] == OptionName) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }

    }

});
