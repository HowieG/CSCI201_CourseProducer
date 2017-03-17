
function toggleSearch() {
	//hidden input that just holds whether we are set to a user search or a movie search
	var searchType = document.getElementById("search_type");

	if(searchType.value === "user") {
		document.getElementById("toggleable_search_type").src = "../img/navbar/clapperboard_icon.png";
		document.getElementById("search_input").placeholder = "Search movies";
		searchType.value = "movie";
	}
	else {
		document.getElementById("toggleable_search_type").src = "../img/navbar/user_icon.png";
		document.getElementById("search_input").placeholder = "Search users";
		searchType.value = "user";
	}
}

//function to navigate to the search jsp
//parameters:
//searchParamKey -- the parameter name for the search param in the ajax URL
//searchTypeKey -- the parameter name for the search type in the ajax URL
//just to be safe, we will pass in the string to use as the parameter name for the search param so we can make sure it matches with what it used in search.jsp
function search(searchParamKey, searchTypeKey){

	var path = "/"+window.location.pathname.split("/")[1];
	window.location.href = path+"/jsp/search.jsp?"+searchParamKey+"="+document.getElementById("search_input").value+"&"+searchTypeKey+"="+
	document.getElementById(searchTypeKey).value;
	return false;
}