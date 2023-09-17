console.log("this is script file");

const toggleSidebar =()=> {
	if ($(".sidebar").is(":visible")) {
		//true
		//sidebar band karna hain 	
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	}
	else {
		//false
		//sidebar show karna hai
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};



//first request - to server to create order 
const paymentStary=()=>{
console.log("Paymentstarted");
}






