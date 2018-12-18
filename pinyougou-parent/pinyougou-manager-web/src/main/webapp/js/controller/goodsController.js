 //控制层 
app.controller('goodsController' ,function($scope,$controller,itemCatService,goodsService,brandService,goodsDescService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				} else {
				    alert(response.message);
                }
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    $scope.statusList = ['未审核','已审核','审核未通过','关闭'];
	//查询所有品牌
    $scope.findCategory = function () {
        $scope.categoryList = [];
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.categoryList[response[i].id] = response[i].name;
            }
        })
    }

    //审核商品
    $scope.setAuditStatus = function (audiStatus) {
        goodsService.update($scope.selectIds,audiStatus).success(function (response) {
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }

        })
    }

    $scope.findByIds = function (brandId,goodsId) {

        goodsService.findOne(goodsId).success(function (response) {
            $scope.commodity = response;
        })

        brandService.findOne(brandId).success(function (response) {
            $scope.brand = response;
        })

        goodsDescService.findOne(goodsId).success(function (response) {
            $scope.goodDesc = response.tbGoodsDesc;
            $scope.goodDesc.itemImages = JSON.parse( $scope.goodDesc.itemImages);
        })
    }
});	
