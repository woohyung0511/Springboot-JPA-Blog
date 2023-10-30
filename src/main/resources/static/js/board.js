let index = {
	init: function() {
		$("#btn-save").on("click", () => { // function(){} , ()=>{} this를 	바인딩 하기 위해!!
			this.save();
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
			//console.log(resp);
			location.href="/";
		}).fail(function(error) {
			alert(JSON.stringify(error));
		});
	},
}

index.init();