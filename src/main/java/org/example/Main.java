package org.example;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private static Connection connection;
    private static boolean loggedIn = true;
    public static void main(String[] args) {
        verMenu();
    }
    public static void openConnection(){
        try{
            connection= DriverManager.getConnection("jdbc:mysql://localhost:3360/anime", "root", "");
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }
    public static void closeConnection(){
        try{
            connection.close();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }
    public static void crearUsuario(Usuario usuario){
        openConnection();
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/anime", "root", "")){
            String query = "INSERT INTO usuarios(nombre, contraseña) VALUES(?,?)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, usuario.getNombre());
                preparedStatement.setString(2, usuario.getContraseña());
                preparedStatement.execute();
                System.out.println("Usuario nuevo creado correctamente.");
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }finally {
            closeConnection();
        }
    }
    public static void loginUsuario(String nombre, String contraseña){
        openConnection();
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/anime", "root", "")){
            String query = "SELECT id_usuario FROM usuarios WHERE nombre=? AND contraseña=?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, contraseña);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    loggedIn = true;
                    System.out.println("Bienvenido "+nombre);
                    System.out.println("Tus animes son: ");
                    mostrarAnimesDeUsuario(resultSet.getInt("id_usuario"));
                }else{
                    System.out.println("Usuario o contraseña incorrectos");
                }
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }finally {
            closeConnection();
        }
    }
    public static void mostrarAnimesDeUsuario(int id_usuario){
        openConnection();
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/anime", "root", "")){
            String query = "SELECT * FROM animes WHERE id_usuario = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setInt(1, id_usuario);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    String nombre = resultSet.getString("nombre");
                    int año = resultSet.getInt("año");
                    String genero = resultSet.getString("genero");
                    String estudio = resultSet.getString("estudio");
                    System.out.println("Nombre: "+nombre+" Año de publicación: "+año+" Genero: "+genero+" Estudio: "+estudio);
                }
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }finally {
            closeConnection();
        }
    }
    public static void crearAnime(Anime anime){
        openConnection();
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/anime", "root", "")){
            String query= "INSERT INTO animes(nombre, año, genero, estudio, id_usuario) VALUES(?,?,?,?,?)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, anime.getNombre() );
                preparedStatement.setInt(2, anime.getAño());
                preparedStatement.setString(3, anime.getGenero());
                preparedStatement.setString(4, anime.getEstudio());
                preparedStatement.setInt(5, anime.getId_usuario());
                if(loggedIn){
                    preparedStatement.execute();
                    System.out.println("Anime insertado correctamente: ");
                }else{
                    System.out.println("Error, usuario no loggeado");
                    verMenu();
                }
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }finally {
            closeConnection();
        }
    }
    public static void verMenu(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Bienvenido. Que quieres hacer?");
            System.out.println("1. Crear Usuario nuevo");
            System.out.println("2. Iniciar sesion");
            System.out.println("3. Subir un nuevo anime");
            System.out.println("4. Salir del menú");
            System.out.println("Selecciona una opcion:");
            int opcion = scanner.nextInt();
            switch (opcion){
                case 1:
                    System.out.println("Introduce tu nuevo nombre");
                    String nombre=scanner.next();
                    System.out.println("Introduce tu nueva contraseña");
                    String contraseña= scanner.next();
                    System.out.println("Repite la contraseña");
                    String contraseñaRepe = scanner.next();
                    if(!Objects.equals(contraseña, contraseñaRepe)){
                        System.out.println("Las contraseñas no coinciden, vuelve a intentarlo");
                        verMenu();
                    }else{
                        Usuario nuevoUsuario= new Usuario(nombre, contraseña);
                        crearUsuario(nuevoUsuario);
                    }
                    break;
                case 2:
                    System.out.println("Introduce tu nombre de usuario");
                    String nombreUsuario = scanner.next();
                    System.out.println("Introduce tu contraseña");
                    String contraseñaUsuario = scanner.next();
                    loginUsuario(nombreUsuario, contraseñaUsuario);
                    break;
                case 3:
                    System.out.println("Introduce el nombre del anime: ");
                    String nombreAnime = scanner.next();
                    System.out.println("Introduce el año de publicación: ");
                    int añoAnime = scanner.nextInt();
                    System.out.println("Introduce el género del anime");
                    String genero = scanner.next();
                    System.out.println("Introduce el estudio del anime");
                    String estudioAnime = scanner.next();
                    System.out.println("A que usuario le gusta el anime? Introduce su id:");
                    int id_usuarioAnime = scanner.nextInt();
                    Anime nuevoAnime = new Anime(nombreAnime, añoAnime, genero, estudioAnime, id_usuarioAnime);
                    crearAnime(nuevoAnime);
                    break;
                case 4:
                    System.out.println("Hasta la proxima!");
                    System.exit(0);
                default:
                    System.out.println("Opcion no valida");
            }
        }
    }
}
class Usuario{
    String nombre, contraseña;
    public String getNombre() {
        return nombre;
    }
    public String getContraseña() {
        return contraseña;
    }
    public Usuario(String nombre, String contraseña){
        this.nombre=nombre;
        this.contraseña=contraseña;
    }
}
class Anime{
    String nombre, genero, estudio;
    int año, id_usuario;
    public Anime(String nombre, int año, String genero, String estudio, int id_usuario){
        this.nombre=nombre;
        this.año=año;
        this.genero=genero;
        this.estudio=estudio;
        this.id_usuario=id_usuario;
    }
    public String getNombre() {
        return nombre;
    }
    public String getGenero() {
        return genero;
    }
    public String getEstudio() {
        return estudio;
    }
    public int getAño() {
        return año;
    }
    public int getId_usuario() {
        return id_usuario;
    }
}