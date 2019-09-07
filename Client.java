import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This code has been adapted from:
 * http://download.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
 */
public class Client {

	static String b = "";// static variable to store the value of cookie
	static String roomname = "";// static variable to store the value of roomname
	final String server = "3901chat/1.0 200 ok";

	/**
	 * This method will check for the user's authentication.It will ask for user's
	 * id and password. if its matching then server will return the success
	 * message(2xx). if user enters wrong username or password then server will say
	 * there is error on client side(4xx).
	 * @param port 
	 * @param host 
	 */
	public boolean auth(String host, int port) throws IOException {
		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			/*
			 * Bluenose defaults to trying to use IPv6, which isn't working well right now
			 * and blocks the Socket call. This next line tells J ava to us IPv4 like the
			 * rest of the world.
			 */

			System.setProperty("java.net.preferIPv4Stack", "true");

			echoSocket = new Socket(host, port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

		} catch (IOException e) {
			System.err.println("Couldn't open socket for the connection.");
			System.exit(1);
		}

		Scanner scan = new Scanner(System.in);// input value for username
		System.out.println("enter the user name");
		String uname = scan.next();
		Scanner scan1 = new Scanner(System.in);// input value for password
		System.out.println("password");
		String pname = scan1.next();
		String st = "AUTH" + " " + uname + " " + "3901chat/1.0" + "\r\n" + "password" + ":" + " " + pname + "\r\n"
				+ "\r\n"; // String to enter a AUTH command
		// System.out.println(st);

		out.println(st);// client to server request for AUTH command

		String st1 = "";
		String a = "";

		int c = 0;
		while ((st1 = in.readLine()) != null) {

			if (c == 1) {

				b = st1;
			}

			a = st1;
			c++;
			System.out.println(a); // Server to client reponse for AUTH command

		}

		// System.out.println(b);

		b = (b.substring(b.indexOf(':') + 1));// extract the value of cookie from the response

		out.close();
		in.close();
		echoSocket.close();
		return true;
	}

	/**
	 * This method will ask user to enter the name of chatroom. To enter in a chat
	 * room user must be authorised. A message to enter valid room returns with a
	 * return code 200 A message to enter valid room returns with a return code 402
	 */
	public boolean enter(String host, int port) throws IOException {

		// String b = auth();

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");

			echoSocket = new Socket(host, port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

		} catch (IOException e) {
			System.err.println("Couldn't open socket for the connection.");
			System.exit(1);
		}

		if (b.equals("")) {// if there is invalid authorization then it should return false.
			return false;
		}

		Scanner scan2 = new Scanner(System.in);
		System.out.println("enter the room name");// ask user enter the room name
		roomname = scan2.next();
		// String to enter into the room number
		String st2 = "ENTER" + " " + roomname + " " + "3901chat/1.0" + "\r\n" + "Cookie:" + b + "\r\n" + "\r\n";
		// System.out.println(st2);
		out.println(st2);// request from client to server for ENTER command

		String cmp = "";
		int c = 0;
		while ((st2 = in.readLine()) != null) {
			if (c == 0) {
				cmp = st2;// String to save the response from server
			}
			System.out.println(st2);
			c++;
		}

		out.close();
		in.close();
		echoSocket.close();

		// if there is a correct response(2xx) from server then it should go for futher
		// execution.
		// Otherwise,program execution should be stopped.
		if (cmp.equals(server)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * This method is used send a text in the room It used current roomname to say
	 * the message A request to post information to the current room returns with a
	 * return code of 200. A request to post when not in a room or to the wrong room
	 * returns with a return code of 402.
	 * @param port 
	 * @param host 
	 */
	public void say(String host, int port) throws IOException {
		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {

			System.setProperty("java.net.preferIPv4Stack", "true");

			echoSocket = new Socket(host, port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

		} catch (IOException e) {
			System.err.println("Couldn't open socket for the connection.");
			System.exit(1);
		}
		Scanner scan4 = new Scanner(System.in);
		System.out.println("enter message you want to say");
		String say = scan4.next();
		int a = say.length();
		// String for a say command
		String st4 = "SAY" + " " + roomname + " " + "3901chat/1.0" + "\r\n" + "Cookie:" + b + "\r\n"
				+ "Content-Length: " + a + "\r\n" + "\r\n" + say;
		// System.out.println(st4);
		out.println(st4); // Client to server request

		while ((st4 = in.readLine()) != null) {

			System.out.println(st4); // Server to client reponse
		}

		out.close();
		in.close();
		echoSocket.close();
	}

	/**
	 * This method is used if we want to exit from a room It uses current roomname
	 * to exit If user not in any room and try to exit from the room then it gives
	 * Invalid room requested
	 * @param port 
	 * @param host 
	 */
	public void exit(String host, int port) throws IOException {
		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");

			echoSocket = new Socket(host, port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

		} catch (IOException e) {
			System.err.println("Couldn't open socket for the connection.");
			System.exit(1);
		}
		// IF user want to exit from the room

		System.out.println("exit from the room");
		// String ex = scan3.next();
		// EXIT command string
		String st3 = "EXIT" + " " + roomname + " " + "3901chat/1.0" + "\r\n" + "Cookie:" + b + "\r\n" + "\r\n";
		System.out.println(st3);
		out.println(st3);

		String ce = "";
		int c1 = 0;
		while ((st3 = in.readLine()) != null) {
			if (c1 == 0) {
				ce = st3;// String to save the response from server
			}
			System.out.println(st3); // Server to client reponse
			c1++;
		}

		out.close();
		in.close();
		echoSocket.close();

		// IF user is successfully able to exit from the room then
		// roomname should be removed so that user can't exit from the roomname again
		if (ce.equals(server)) {
			roomname = "";
		} else {
			System.out.println("exit nt correct");
		}
	}

	/**
	 * This method is used to deregister the user from server It should only execute
	 * if user is authorised
	 * @param port 
	 * @param host 
	 */
	public void bye(String host, int port) throws IOException {

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {

			System.setProperty("java.net.preferIPv4Stack", "true");

			echoSocket = new Socket(host, port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

		} catch (IOException e) {
			System.err.println("Couldn't open socket for the connection.");
			System.exit(1);
		}

		// To check whether user is authenticated or not
		if (!b.equals("")) {
			System.out.println("byeeeeeeeeeeeee");

			// String for BYE command
			String st5 = "BYE" + " " + "BYE" + " " + "3901chat/1.0" + "\r\n" + "Cookie:" + b + "\r\n" + "\r\n";
			System.out.println(st5);
			out.println(st5);

			while ((st5 = in.readLine()) != null) {

				System.out.println(st5);

			}

		}

		out.close();
		in.close();
		echoSocket.close();

	}

	/**
	 * This method should execute if user is authorised and entered into the correct
	 * room It ask if you want to SAY or EXIT or ENTER or BYE from the program If we choose say then
	 * the body of the SAY message contains the text to say in the room. 
	 * It also allows user to Enter in the different room after entering in one room and send text
	 * correct then it will exit.
	 * User can also any time deregister from the server
	 * @param port 
	 * @param host 
	 */
	public void exitorsay(String host, int port) throws IOException {
		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		// If AUTH and ENTER method works then only user can enter into chat room
		if (auth(host, port) && enter(host, port)) {

			String enter = "";
			while (enter.equals("1") || enter.equals("2") || enter.equals("3") || enter.equals("4")
					|| enter.equals("")) {
				// User can decide whether they want to say some message or want to exit
				System.out.println("select choice 1 0r 2 or 3");
				System.out.println("1.message");
				System.out.println("2.exit");
				System.out.println("3.enter different room");
				System.out.println("4.byeee");
				System.out.println("Enter your choice");
				Scanner s = new Scanner(System.in);
				enter = s.next();

				if (enter.equals("1")) {// if user want to say something
					say(host, port);
				} else if (enter.equals("2")) {//user will exit from the current room
					exit(host, port);
				} else if (enter.equals("3")) {//user can enter into different room
					enter(host, port);
				} else if (enter.equals("4")) {//user can deregister from thw server
					bye(host, port);
					break;
				}
			}
		}

	}

	
	public static void main(String args[]) throws IOException {
		String host = args[1];
		int port = Integer.parseInt(args[3]);
		Client c = new Client();
		c.exitorsay(host,port);

	}
}
