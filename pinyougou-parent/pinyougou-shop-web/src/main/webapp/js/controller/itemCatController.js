 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}

    $scope.selectHome = {id:0,name:'顶级分类列表'} ;
	//保存
	$scope.add=function(entity,grade){
	    if (grade == 1) {
            entity.parentId = $scope.selectHome.id;
        }
        if (grade == 2) {
            entity.parentId = $scope.select_1.id;
        }
        if (grade == 3) {
            entity.parentId = $scope.select_2.id;
        }
        itemCatService.add(entity).success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.findByParentId(entity.parentId);//重新加载
                }else{
                    alert(response.message);
                }
            }
        );



    }
	
	 
	//批量删除 
	$scope.dele=function(grade){
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
				    if (grade==1) {
                        $scope.findByParentId($scope.selectHome.id);
                    }
                    if (grade==2) {
                        $scope.findByParentId($scope.select_1.id);
                    }
                    if (grade==3) {
                        $scope.findByParentId($scope.select_2.id);
                    }
				}						
			}		
		);				
	}

	$scope.findByParentId = function (parentId) {
		itemCatService.findByParentId(parentId).success(function (response) {
			$scope.list = response;
        })
    }

    $scope.grade = 1;

	$scope.setGrade = function(newGrade) {
	    $scope.grade = newGrade;
	    return $scope.grade;
    }

	$scope.selectByGrade = function (grade,entity) {
        if (grade == 1) {
            $scope.select_1 = null;
            $scope.select_2 = null;
        }
        if(grade == 2) {
            $scope.select_1 = entity;
            $scope.select_2 = null;
        }
        if (grade == 3)  {
            $scope.select_2 = entity;
        }

        $scope.findByParentId(entity.id);
    }
    
});	
