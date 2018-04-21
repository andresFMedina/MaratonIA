package maratonia;

import clases.Arbol;
import clases.Mover;
import clases.Pelear;
import clases.Robot;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdministrarArbol {

    public AdministrarArbol() {
    }

    public int calcularHeuristica(Arbol nodo) {
        int tab[][] = nodo.getNodo();
        int acumulador = 0;
        Robot robots[] = nodo.getRobots();
        for (int i = 0; i < robots.length; i++) {
            //Calcula la posicion del robot con respecto a la meta
            acumulador += tab[0].length - robots[i].getPosicion();

        }
        acumulador /= 4;

        return acumulador;
    }
//Ahora sirve pero toca probarlo bien         

    public int calcularCosto(Arbol nodo) {

        int acumulador = 0;
        Robot robots[] = nodo.getRobots();
        for (int i = 0; i < robots.length; i++) {
            //Calcula la posicion del robot con respecto a la meta
            acumulador += robots[i].getPelear().getTiempo();

        }

        return acumulador;
    }

    ////////////////////////////////////////////////////////////         
    public Arbol crearNodo(int id, Arbol padre, int enemigos[], int robotspelea[], int robotsres[], int idrobot) {
        Arbol p = null;
        int ene[] = null;
        int robotspel[]=null;
        int restrobots[]=null;
        try {
            p = (Arbol) padre.clone();
            ene = enemigos.clone();
            robotspel=robotspelea.clone();
            
            if(robotsres!=null){
                restrobots=robotsres.clone();
            }
            else{
                restrobots=null;
            }
            
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(AdministrarArbol.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*System.out.println("Enemigos:");
        for (int i = 0; i < ene.length; i++) {
            System.out.print(ene[i] + " ");
        }
        System.out.println("");*/
        int idPadre = p.getPadre(); //Deberia ser getId(?
        int tab[][] = new int[4][p.getNodo()[0].length];
        tab[0] = p.getNodo()[0].clone();
        tab[1] = p.getNodo()[1].clone();
        tab[2] = p.getNodo()[2].clone();
        tab[3] = p.getNodo()[3].clone();

        Arbol nodo = new Arbol(id, tab, idPadre, 0, 0, 0, false);
        Robot robots[] = new Robot[4];
        try {
            for (int i = 0; i < robots.length; i++) {
                robots[i] = (Robot) p.getRobots()[i].clone();
                Pelear pe = (Pelear) robots[i].getPelear().clone();
                Mover mo = (Mover) robots[i].getMover().clone();
                robots[i].setMover(mo);
                robots[i].setPelear(pe);

            }

          
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(AdministrarArbol.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < robotspel.clone().length; i++) {

            robots[robotspel.clone()[i]].getMover().setCasillas(2);
            moverRobot(robots[robotspel.clone()[i]], nodo.getNodo());
            //Estas dos lineas son necesarias para que no se contabilice los tiempos calculados anteriormente
            robots[robotspel.clone()[i]].getPelear().setTiempo(0);
            robots[robotspel.clone()[i]].getPelear().setEnemigos(null);
        }
        robots[idrobot].getPelear().tiempoPelea(ene.clone());
        robots[idrobot].getPelear().setEnemigos(ene.clone());

        peleaRobot(robots[idrobot], nodo.getNodo());
        if (restrobots != null) {

            if (robots[restrobots.clone()[0]].getPosicion() != robots[restrobots.clone()[0]].getPosicion()) {

                for (int i = 0; i < restrobots.clone().length; i++) {

                    if (robots[robotspel.clone()[i]].getPelear().getTiempo() != 0) {
                        peleaRobot(robots[restrobots.clone()[i]], nodo.getNodo());
                    } else {
                        moverRobot(robots[restrobots.clone()[i]], nodo.getNodo());
                    }

                }
            }
        }
        nodo.setRobots(robots);

        int heuristica = calcularHeuristica(nodo);
        nodo.setHeuristica(heuristica);
        int costo = calcularCosto(nodo);
        nodo.setCosto(costo);
        nodo.setSuma(costo + heuristica);
        verNodos(nodo);
        return nodo;
    }

    public Arbol inicializarNodo(int id, int padre, int tab[][], Robot robots[]) {
        //nodo.setExpandido(true);

        Arbol nodo = new Arbol(id, tab, padre, 0, 0, 0, false);

        nodo.setRobots(robots);
        int heuristica = calcularHeuristica(nodo);
        nodo.setHeuristica(heuristica);
        int costo = calcularCosto(nodo);
        nodo.setCosto(costo);
        nodo.setSuma(costo + heuristica);
        return nodo;
    }

    public void moverRobot(Robot robot, int tablero[][]) {

        int mov = robot.getMover().moverA(robot.getPosicion());

        tablero[robot.getId() - 1][mov] = robot.getId();
        tablero[robot.getId() - 1][robot.getPosicion()] = 0;
        robot.setPosicion(mov);
        robot.getMover().setCasillas(1);

    }

    public void peleaRobot(Robot robot, int tablero[][]) {
        int largo = robot.getPelear().getEnemigos().length;
        int posicion = robot.getPelear().getPosenemigo();
        int array[] = robot.getPelear().getEnemigos();

        int fuerza = robot.getPelear().getFuerza();

        if (array[posicion] != 0) {
            if (array[posicion] == fuerza) {
                array[posicion] = 0;
                robot.getPelear().setEnemigos(array);
                robot.getPelear().setTiempo(robot.getPelear().getTiempo() - 1);
                tablero[posicion][robot.getPosicion() + 1] = 0;
                robot.getPelear().setPosenemigo(posicion + 1);
                /////////////////////Cambio/////////////////////////////////////////////////////////    
                //Cambie esta logica porque solo funciona siempre y cuando todos los enemigos sean iguales
                //Ahora puse un contador de atributo para contar cuanto le falta al robot para acabar con cada enemigo
            } else {
                if (robot.getPelear().getTiempoenemigo() == 0) {
                    robot.getPelear().setTiempoenemigo(2);
                }

                robot.getPelear().setTiempo(robot.getPelear().getTiempo() - 1);
                robot.getPelear().setTiempoenemigo(robot.getPelear().getTiempoenemigo() - 1);
                if (robot.getPelear().getTiempoenemigo() == 0) {
                    array[posicion] = 0;
                    robot.getPelear().setEnemigos(array);
                    tablero[posicion][robot.getPosicion() + 1] = 0;
                    robot.getPelear().setPosenemigo(posicion + 1);
                }

            }
            /////////////////////Fin Cambio/////////////////////////////////////////////////////////    
        }
    }

    //Metodo para calcular la heurisca de cada nodo
    public void verNodos(Arbol nodo) {
        System.out.println("\nArbol Nodo " + nodo.getId() + ":");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < nodo.getNodo()[0].length; j++) {
                System.out.print(nodo.getNodo()[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("Suma: " + nodo.getSuma());
        System.out.println("Piedra " + nodo.getRobots()[0].getPelear().getTiempo());
        System.out.println("Papel " + nodo.getRobots()[1].getPelear().getTiempo());
        System.out.println("Tijera " + nodo.getRobots()[2].getPelear().getTiempo());
        System.out.println("Pistola " + nodo.getRobots()[3].getPelear().getTiempo());
        System.out.println("\n");
    }
}
