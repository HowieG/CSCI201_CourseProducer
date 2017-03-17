
//function to add or remove a username from the loggedin user's following list
//parameters:
//username -- username to add or remove
//loggedInUsername -- username of logged in user
//toFollow -- bool that if true means we should add the username to the following list (otherwise remove)
//usernameKey -- the parameter name for the username in the ajax URL
//toFollowKey -- the parameter name for the toFollow bool in the ajax URL
function follow(username, loggedInUsername, toFollow, usernameKey, toFollowKey){
	//make an ajax call to ChangeFollowServlet
	var xhttp = new XMLHttpRequest();
	console.log("/"+window.location.pathname.split("/")[1]);
	xhttp.open("GET", "/"+window.location.pathname.split("/")[1]+"/ChangeFollowServlet?"+usernameKey+"="+
			username+"&"+toFollowKey+"="+toFollow, false);
	xhttp.send();
	
	//if the loggedInUser added the username to their following list
	if (toFollow){
		//create a new h2 element
	    var h2 = document.createElement('h2');
	    //set the id to loggedin_follower (so we know the exact element that references the loggedin user's username)
	    h2.id = "loggedin_follower";
	    //create and a element, and set the href to navigate to the user_profile jsp
	    var a = document.createElement('a');
	    a.href = "/"+window.location.pathname.split("/")[1]+"/jsp/user_profile.jsp?"+usernameKey+"="+loggedInUsername;
	    //create a text node with the loggedInUsername and append it as a child to the a element
	    var text = document.createTextNode(loggedInUsername);
	    a.appendChild(text);
	    //append the a element as a child of the h2 element
	    h2.appendChild(a);
	    //append the h2 element as a child of the followers div
	    document.getElementById('followers').appendChild(h2);
	    //switch visiblity of unfollow and follow buttons
	    document.getElementById("follow_button").style.display = "none";
	    document.getElementById("unfollow_button").style.display = "block";
		     
	}
	//if the loggedIsUser removed the username from their following list
	else{
		//remove the loggedin_follower element from the followers div
	    document.getElementById('loggedin_follower').remove();
	    //switch visibility of unfollow and follow buttons
	    document.getElementById("unfollow_button").style.display = "none";
        document.getElementById("follow_button").style.display = "block";		
	}
	
	return false;
}