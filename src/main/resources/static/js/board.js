let index = {
	init: function() {
		$("#btn-save").on("click", () => { // function(){} , ()=>{} this를 	바인딩 하기 위해!!
			this.save();
		});
		$("#btn-delete").on("click", () => { // function(){} , ()=>{} this를 	바인딩 하기 위해!!
			this.deleteById();
		});
		$("#btn-update").on("click", () => { // function(){} , ()=>{} this를 	바인딩 하기 위해!!
			this.update();
		});
	},

	save: function() {
		//alert('user의 save함수 호출');
		let data = {
			title: $("#title").val(),
			content: $("#content").val(),

		};

		$.ajax({
			type: "post",
			url: "/api/board",
			data: JSON.stringify(data), 
			contentType: "application/json; charset=utf-8",
			dataType: "json" 
		}).done(function(resp) {
			alert("글 쓰기가 완료되었습니다.")
			location.href="/";
		}).fail(function(error) {
			alert(JSON.stringify(error));
		});
	},
	
	deleteById: function() {
		let id = $("#id").text();
		
		$.ajax({
			type: "delete",
			url: "/api/board"+id,
			data: JSON.stringify(data), 
		}).done(function(resp) {
			alert("삭제가 완료되었습니다.")
			location.href="/";
		}).fail(function(error) {
			alert(JSON.stringify(error));
		});
	},
	
	update: function() {
		let id = $("#id").val();
		
		let data = {
			title: $("#title").val(),
			content: $("#content").val(),

		};

		$.ajax({
			type: "put",
			url: "/api/board"+id,
			data: JSON.stringify(data), 
			contentType: "application/json; charset=utf-8",
			dataType: "json" 
		}).done(function(resp) {
			alert("글 수정이 완료되었습니다.")
			location.href="/";
		}).fail(function(error) {
			alert(JSON.stringify(error));
		});
	},
}

index.init();