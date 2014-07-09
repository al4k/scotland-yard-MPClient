package ai;

import java.util.*;
import state.*;
import game.TextOutput;
import graph.*;
import gui.PlayerInformationReader;

public class SmartAI extends AI 
{
    boolean dangerOverride = false, forcesecret = false, disablesecret = false;
    int movessince, displacement, taxiTickets, busTickets, undergroundTickets, playerId, mrXId, dangermode, tempdisplacement, spareblack;
    public static int totalturns;
    Graph mygraph;
    HashMap<String, Double> dist;
    List<Node> shortestPath, solvedNodes, validMoves;
    List<Node> potentialNodes = new ArrayList<Node>();
    List<Initialisable.TicketType> ticketTypes;
    List<Integer> quadrants = new ArrayList<Integer>();
    Move finalMove;
    Node startNode, endNode, lastLocation, myposition, mrxposition;
    PlayerInformationReader pi;
    List<Integer> nodescores = new ArrayList<Integer>();
    List<Node> detectiveBrain = new ArrayList<Node>();
    List<Node> bestnodes = new ArrayList<Node>();
    public static Node caughtNode;

    @Override
    public  Move getMove(int id)
    {
        InitialiseValues(id);
        //System.out.println("[PATHFINDING]: Pathfinding started for player " + playerId + " at position " + aiReadable.getNodeId(playerId));

        return GetAIState();
    }

    private void InitialiseValues(int id)
    {
        pi = new PlayerInformationReader(aiReadable, aiReadable);
        mygraph = aiReadable.getGraph();
        playerId = id;
        dangermode = 0;
        shortestPath = new ArrayList<Node>();
        dist = new HashMap<String, Double>();
        taxiTickets = aiReadable.getNumberOfTickets(Initialisable.TicketType.Taxi, playerId);
        busTickets = aiReadable.getNumberOfTickets(Initialisable.TicketType.Bus, playerId);
        undergroundTickets = aiReadable.getNumberOfTickets(Initialisable.TicketType.Underground, playerId);
        myposition = mygraph.find(aiReadable.getNodeId(playerId) - 1);
        mrXId = aiReadable.getMrXIdList().get(0);
        mrxposition = mygraph.find(aiReadable.getNodeId(mrXId) - 1);
        totalturns = aiReadable.getMoveList(mrXId).size();
        ticketTypes = aiReadable.getMoveList(mrXId);
        validMoves = new ArrayList<Node>();

        if(totalturns == 0 || totalturns == 3 || totalturns % 5 == 3)
        {
            displacement = 0;
            lastLocation = mrxposition;
            potentialNodes.add(lastLocation);
            if(playerId == mrXId)
            {
                if(aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId) > 1 && disablesecret == false)
                {
                    dangermode = 2;
                }
            }
        }
    }

    private Move GetAIState()
    {
        if(playerId == mrXId)
        {

            detectiveBrain.clear();
            return GetBestMrXMove(0);
        }

        else
        {
            //Detective States (dState): 0 = Chase Mode 1 = Prediction Mode
            if(totalturns == 3 || totalturns % 5 == 3 || totalturns == 1)
            {
                endNode = mygraph.find(aiReadable.getNodeId(mrXId) - 1);
                return GetBestDetectiveMove (0);
            }

            else
            {
                return GetBestDetectiveMove (1);
            }
        }
    }

    private void CalculateQuadrants()
    {
        double distance = 0;
        double tempdistance = 0;
        int i = 0;
        quadrants.clear();

        while(quadrants.size() < 4)
        {
            quadrants.add(0);
        }

        while(i < aiReadable.getDetectiveIdList().size())
        {
            int node = aiReadable.getNodeId(aiReadable.getDetectiveIdList().get(i));
            if(node < 101)
            {
                startNode = mygraph.find(node - 1);
                endNode = mygraph.find(17);
                tempdistance = dijkstraSimple();
                endNode = mygraph.find(16);
                distance = dijkstraSimple();
                if(tempdistance < distance)
                {
                    quadrants.set(0, quadrants.get(0) + 1);
                }
                else
                {
                    quadrants.set(1, quadrants.get(1) + 1);
                }
            }
            else
            {
                startNode = mygraph.find(node - 1);
                endNode = mygraph.find(175);
                tempdistance = dijkstraSimple();
                endNode = mygraph.find(174);
                distance = dijkstraSimple();
                if(tempdistance < distance)
                {
                    quadrants.set(2, quadrants.get(2) + 1);
                }
                else
                {
                    quadrants.set(3, quadrants.get(3) + 1);
                }
            }
            i++;
        }
        
        int node = Integer.parseInt(lastLocation.name().trim());
        if(node < 101)
        {
            startNode = mygraph.find(node - 1);
            endNode = mygraph.find(17);
            tempdistance = dijkstraSimple();
            endNode = mygraph.find(16);
            distance = dijkstraSimple();
            if(tempdistance < distance)
            {
                quadrants.set(0, quadrants.get(0) + 1);
            }
            else
            {
                quadrants.set(1, quadrants.get(1) + 1);
            }
        }
        else
        {
            startNode = mygraph.find(node - 1);
            endNode = mygraph.find(175);
            tempdistance = dijkstraSimple();
            endNode = mygraph.find(174);
            distance = dijkstraSimple();
            if(tempdistance < distance)
            {
                quadrants.set(2, quadrants.get(2) + 1);
            }
            else
            {
                quadrants.set(3, quadrants.get(3) + 1);
            }
        }
    }

    private List<Node> GetQuadrantMoves (List<Node> neighbours)
    {
        List<Node> bestMoves = new ArrayList<Node>();
        int i = 0;
        double tempdistance = 0;
        double distance = 10000;
        int j = 0;
        int lowest = 1000;
        List<Integer> qindexes = new ArrayList<Integer>();
        Node closest = null;

        while(j < quadrants.size())
        {
            if(quadrants.get(j) < lowest)
            {
                qindexes.clear();
                lowest = quadrants.get(j);
                qindexes.add(j);
            } 
            else if(quadrants.get(j) == lowest)
            {
                qindexes.add(j);
            }
            j++;
        }

        j = 0;

        while(j < qindexes.size())
        {
            startNode = myposition;
            if(qindexes.get(j) == 0)
            {
                endNode = mygraph.find(57);
            } 
            else if(qindexes.get(j) == 1)
            {
                endNode = mygraph.find(28);
            }
            else if(qindexes.get(j) == 2)
            {
                endNode = mygraph.find(177);
            }
            else if(qindexes.get(j) == 3)
            {
                endNode = mygraph.find(160);
            }

            tempdistance = dijkstraSimple();

            if(tempdistance < distance)
            {
                closest = endNode;
            }

            j++;
        }

        distance = 10000;

        while(i < neighbours.size())
        {
            startNode = neighbours.get(i);
            endNode = closest;
            tempdistance = dijkstraSimple();

            if(tempdistance < distance)
            {
                distance = tempdistance;
                bestMoves.clear();
                bestMoves.add(neighbours.get(i));
            } 
            else if(tempdistance == distance)
            {
                bestMoves.add(neighbours.get(i));
            } 

            i++;
        }

        return bestMoves;
    }

    private Move GetBestMrXMove(int state)
    {
        startNode = myposition;
        List<Node> bestMoves = new ArrayList<Node>();
        List<Node> neighbours = new ArrayList<Node>();
        Node bestNode = startNode;

        CalculateQuadrants();

        neighbours = GetValidMoves(myposition, playerId, taxiTickets, busTickets, undergroundTickets);

        if(dangerOverride == false || movessince == 1)
        {
            neighbours = RemoveDangerousNodes(neighbours);
        }
        else
        {
            neighbours = CalculateBestDoubleNode(neighbours);
        }

        if(neighbours.size() == 0)
        {
            bestNode = myposition;
        }
        else
        {
            if(dangermode == 2 && disablesecret == true)
            {
                dangermode = 0;
            }

            if((totalturns == 0 || totalturns == 3 || totalturns % 5 == 3) && dangermode == 0)
            {
                spareblack += 1;
            }

            bestMoves = GetQuadrantMoves(neighbours);
            bestMoves = GetFurthestFromLastLocation(bestMoves);
            bestMoves = GetMostNeighbours(bestMoves);
            bestNode = GetTotalFurthestNeighbour(bestMoves);
            caughtNode = bestNode;
        }

        finalMove = ConvertNodeToMove(bestNode);

        /*if(totalturns == 0)
        {
            System.out.println("Mr. X's position is " + myposition.name().trim());
        }
        if(totalturns == 2 || totalturns % 5 == 2)
        {
            System.out.println("Mr. X's position is " + finalMove.location);
        }*/
        //System.out.println("[PATHFINDING]: DisableSecret value is " + disablesecret);
        //System.out.println("[PATHFINDING]: Mr. X has Double Tickets: " + aiReadable.getNumberOfTickets(Initialisable.TicketType.DoubleMove, playerId) + " and secret tickets: " + aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId));

        if(totalturns != 0 && totalturns != 2 && totalturns % 5 != 2)
        {
            if(finalMove.type == Initialisable.TicketType.Underground && aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId) > 1)
            {
                finalMove.type = Initialisable.TicketType.SecretMove;
                spareblack -= 1;
            }

            if(spareblack > 0 && disablesecret == false && finalMove.type != Initialisable.TicketType.SecretMove && finalMove.type != Initialisable.TicketType.DoubleMove && aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId) > 0)
            {
                finalMove.type = Initialisable.TicketType.SecretMove;
                spareblack -= 1; 
            }
        }

        //System.out.println("[PATHFINDING]: Mr. X moving from " + aiReadable.getNodeId(playerId) + " to " + finalMove.location + " with ticket type " + finalMove.type);

        return finalMove;
    }

    private Move GetBestDetectiveMove(int state)
    {
        startNode = myposition;
        List<Node> neighbours = GetValidMoves(myposition, playerId, taxiTickets, busTickets, undergroundTickets);
        if(neighbours.contains(mrxposition))
        {
            //System.out.println("[PATHFINDING]: Mr. X is a dead bitch");
            /*try {
                Thread.sleep(1000000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }*/
        }
        Node bestNode = startNode;
        List<Integer> bestindex = new ArrayList<Integer>();

        //Uncomment to activate detective aggressive mode
        //state = 0;

        if(neighbours.size() == 0)
        {
            bestNode = myposition;
        }
        else
        {
            if (state == 0)
            {
                //System.out.println("[PATHFINDING]: Finding best detective state 0 position");
                bestNode = GetFastestNode(neighbours);
            }
            else
            {
                //System.out.println("[PATHFINDING]: Finding best detective state 1 position");
                if(tempdisplacement != displacement)
                {
                    CalculatePotentialNodes();
                    tempdisplacement = displacement;
                    CalculateScore1();
                    CalculateScore2();
                    CalculateScore3();
                    bestnodes = GetBestNodes();
                    //System.out.println("[PATHFINDING]: Recalculating potential nodes");
                }
                bestNode = GetClosestNode(neighbours, bestnodes);
            }
        }

        finalMove = ConvertDetNodeToMove(bestNode);

        //System.out.println("[PATHFINDING]: Detective " + playerId + " Moving from " + aiReadable.getNodeId(playerId) + " to " + finalMove.location + " with ticket type " + finalMove.type);

        return finalMove;
    }

    private List<Node> GetFurthestFromLastLocation(List <Node> neighbours)
    {
        int i = 0;
        endNode = lastLocation;
        double distance = 0;
        double tempdistance = 0;
        List<Node> bestMoves = new ArrayList<Node>();

        while(i < neighbours.size())
        {
            startNode = neighbours.get(i);
            tempdistance = dijkstraSimple();
            if(tempdistance > distance)
            {
                distance = tempdistance;
                bestMoves.clear();
                bestMoves.add(neighbours.get(i));
            }
            else if(tempdistance == distance)
            {
                bestMoves.add(neighbours.get(i));
            }
            i++;
        }
        return bestMoves;
    }

    private List<Node> CalculateBestDoubleNode(List<Node> neighbours)
    {
        List<Node> secondneighbours = new ArrayList<Node>();
        int mostoptions = 0;
        List<Node> returnlist = new ArrayList<Node>();
        int i = 0;
        // System.out.println("[PATHFINDING]: Calculating best double node");

        while(i < neighbours.size())
        {
            // System.out.println("[PATHFINDING]: Removing dangerous nodes from node " + neighbours.get(i).name().trim());
            secondneighbours = GetValidMoves(neighbours.get(i), playerId, taxiTickets, busTickets, undergroundTickets);
            secondneighbours.remove(neighbours.get(i));
            secondneighbours = RemoveDangerousNodes(secondneighbours);
            if(secondneighbours.size() > mostoptions)
            {
                //System.out.println("[PATHFINDING]: Better double node found, Node: " + neighbours.get(i).name().trim());
                mostoptions = secondneighbours.size();
                returnlist.clear();
                returnlist.add(neighbours.get(i));
            } 
            else if(secondneighbours.size() == mostoptions)
            {
                //System.out.println("[PATHFINDING]: Equal double node found, Node: " + neighbours.get(i).name().trim());
                returnlist.add(neighbours.get(i));
            }
            i++;
        }
       
        if(returnlist.size() == 0)
        {
            returnlist.clear();
            i = 0;
            while(i < neighbours.size())
            {
                //System.out.println("[PATHFINDING]: No safe moves. Death is almost inevitable");
                returnlist.add(neighbours.get(i));
                i++;
            }
        }


        return returnlist;
    }

    private void CalculateScore1()
    {
        nodescores.clear();
        int i = 0;
        int j = 0;
        double distance = 10000;
        double tempdistance = 0;
        Node closestdetectivenode = null;
        int smallestmoves = 0;

        while(i < potentialNodes.size())
        {
            startNode = potentialNodes.get(i);
            smallestmoves = 0;
            j = 0;
            distance = 10000;
            while(j < aiReadable.getDetectiveIdList().size())
            {
                endNode = mygraph.find(aiReadable.getNodeId(aiReadable.getDetectiveIdList().get(j)) - 1);
                tempdistance = dijkstraSimple();
                if(tempdistance < distance)
                {
                    distance = tempdistance;
                    closestdetectivenode = endNode;
                    smallestmoves = shortestPath.size() - 1;

                }
                j++;
            }
            if(smallestmoves > 5)
            {
                smallestmoves = 5;
            }
            nodescores.add(smallestmoves);
            //System.out.println("[PATHFINDING]: Distance from node " + potentialNodes.get(i).name().trim() + " to the nearest detective is " + smallestmoves);
            i++;
        }
    }

    private void CalculateScore2()
    {
        int i = 0;
        int j = 0;
        double tempdistance = 0;
        int score = 0;

        while(i < potentialNodes.size())
        {
            startNode = potentialNodes.get(i);
            j = 0;
            tempdistance = 0;
            while(j < aiReadable.getDetectiveIdList().size())
            {
                endNode = mygraph.find(aiReadable.getNodeId(aiReadable.getDetectiveIdList().get(j)) - 1);
                tempdistance += (dijkstraSimple() - 1);
                j++;
            }
            tempdistance /= aiReadable.getDetectiveIdList().size();
            score = (int)Math.round(tempdistance);
            if(score > 5)
            {
                score = 5;
            }
            nodescores.set(i, nodescores.get(i) + score);
            //System.out.println("[PATHFINDING]: Node " + potentialNodes.get(i).name().trim() + " has average distance of " + score);
            //System.out.println("[PATHFINDING]: Node " + potentialNodes.get(i).name().trim() + " has total score of " + nodescores.get(i));
            i++;
        }
    }

    private void CalculateScore3()
    {
        int i = 0;
        int score = 0;
        List<Node> neighbours = new ArrayList<Node>();

        while(i < potentialNodes.size())
        {
            neighbours = findNeighbours(potentialNodes.get(i).name().trim());
            score = neighbours.size() - 1;
            if(score > 5)
            {
                score = 5;
            }
            nodescores.set(i, nodescores.get(i) + score);
            //System.out.println("[PATHFINDING]: Node " + potentialNodes.get(i).name().trim() + " has neighbour score of " + score);
            //System.out.println("[PATHFINDING]: Node " + potentialNodes.get(i).name().trim() + " has total score of " + nodescores.get(i));
            i++; 
        }
        //System.out.println("[PATHFINDING]: Mr X's actual position is " + mrxposition.name().trim());
    }

    private List<Node> GetBestNodes()
    {
        List<Integer> bestindex = new ArrayList<Integer>();
        int bestscore = 0;
        int i = 0;
        int maxloops = 0;

        bestnodes.clear();

        bestscore = Collections.max(nodescores);
        //System.out.println("[PATHFINDING]: Potential nodes size is " + potentialNodes.size());
        //System.out.println("[PATHFINDING]: Nodescores size is " + nodescores.size());
        //System.out.println("[PATHFINDING]: Detective list size is " + aiReadable.getDetectiveIdList().size());

        if(nodescores.size() > aiReadable.getDetectiveIdList().size())
        {
            maxloops = aiReadable.getDetectiveIdList().size(); 
        }
        else
        {
            maxloops = nodescores.size();
        }

        while(bestindex.size() < maxloops)
        {
            while(i < nodescores.size())
            {
                if(nodescores.get(i) == bestscore)
                {
                    //System.out.println("[PATHFINDING]: Added a best index " + i);
                    bestindex.add(i);
                }
                i++;
                //System.out.println("[PATHFINDING]: Checking nodescore loop");
            }
            //System.out.println("[PATHFINDING]: Main Loop terminated");
            i = 0;
            bestscore -= 1;
        }

        i = 0;
        bestnodes.clear();
        //System.out.println("[PATHFINDING]: Best index size is " + bestindex.size());

        while(bestnodes.size() < bestindex.size())
        {
            //System.out.println("[PATHFINDING]: Attempting to add a best node");
            bestnodes.add(potentialNodes.get(bestindex.get(i)));
            //System.out.println("[PATHFINDING]: Best node " + potentialNodes.get(bestindex.get(i)).name().trim() + " added");
            i++;
        }

        return bestnodes;
    }

    private Node GetFastestNode(List<Node> neighbours)
    {
        Node bestNode = null;
        int i = 0;
        double distance = 10000, tempdistance = 0;

        endNode = mrxposition;
        lastLocation = endNode;

        while(i < neighbours.size())
        {
            startNode = neighbours.get(i);
            tempdistance = dijkstraSimple();
            if(tempdistance < distance)
            {
                bestNode = startNode;
                distance = tempdistance;
            }

            i++;
        }

        return bestNode;

    }

    private void CalculatePotentialNodes()
    {
        List<Edge> edges = new ArrayList<Edge>();
        solvedNodes = new ArrayList<Node>();
        int j = 0;
        if(totalturns > 1)
        {
            if(displacement < 2)
            {
                displacement = 1;
                potentialNodes.clear();
                potentialNodes.add(lastLocation);
                //System.out.println("[PATHFINDING]: Official location is " + potentialNodes.get(0).name());
            }
        }

        int i = displacement - 1;

        while(i < displacement)
        {
            while(potentialNodes.size() > 0)
            {
                edges = mygraph.edges(potentialNodes.get(j).name().trim());
                int z = 0;
                while(z < edges.size())
                {
                    if(convertEdgeTypeToTicketType(edges.get(z).type()) != ticketTypes.get(ticketTypes.size() - (i+1)) && ticketTypes.get(ticketTypes.size() - (i+1)) != Initialisable.TicketType.DoubleMove && ticketTypes.get(ticketTypes.size() - (i+1)) != Initialisable.TicketType.SecretMove)
                    {
                        edges.remove(edges.get(z));
                        z -= 1;
                    }
                    z++;
                }

                if(edges.size() > 0)
                {
                    for (Edge e2 : edges)
                    {
                        if(e2.id1().equals(potentialNodes.get(j).name().trim())){
                            solvedNodes.add(mygraph.find(Integer.parseInt(e2.id2()) - 1));
                        }

                        else
                        {
                            solvedNodes.add(mygraph.find(Integer.parseInt(e2.id1()) - 1));   
                        }
                    }
                }
                potentialNodes.remove(j);
            }

            j = 0;

            while(j < solvedNodes.size())
            {
                // System.out.println("[PATHFINDING]: Adding solvedNode " + solvedNodes.get(j).name());
                if(!potentialNodes.contains(solvedNodes.get(j)))
                {
                    potentialNodes.add(solvedNodes.get(j));
                }

                j++;
            }
            solvedNodes.clear();
            i++;
        }

        i = 0;
        j = 0;

        while(i < potentialNodes.size())
        {
            while(j < aiReadable.getDetectiveIdList().size())
            {
                if(potentialNodes.get(i) == mygraph.find(aiReadable.getNodeId(aiReadable.getDetectiveIdList().get(j)) - 1))
                {
                    potentialNodes.remove(i);
                    j = aiReadable.getDetectiveIdList().size();
                    i -= 1;
                }
                j++;
            }
            i++;
        }
    }

    private Node GetClosestNode(List<Node> neighbours, List<Node> bestnodes)
    {
        //System.out.println("[PATHFINDING]: Finding closest node");
        Node bestNode = null;
        int i = 0;
        double distance, tempdistance;
        Node closestNode = null;

        while(i < detectiveBrain.size())
        {
            if(bestnodes.contains(detectiveBrain.get(i)) && bestnodes.size() > 1)
            {
                bestnodes.remove(detectiveBrain.get(i));
            }
            i++;
        }

        i = 0;

        //System.out.println("[PATHFINDING]: Listing potential nodes of size " + potentialNodes.size());

        if(bestnodes.size() > 0)
        {
            distance = 10000;

            while(i < bestnodes.size())
            {
                //System.out.println("[PATHFINDING]: Potential node " + i + " is Node " + potentialNodes.get(i).name().trim());
                startNode = myposition;
                endNode = bestnodes.get(i);
                tempdistance = dijkstraSimple();
                if(tempdistance < distance)
                {
                    distance = tempdistance;
                    closestNode = endNode;
                }
                i++;
            }
            detectiveBrain.add(closestNode);
            endNode = closestNode;
        }

        else
        {
            //System.out.println("[PATHFINDING]: Updated endNode as no moves available");
            if(lastLocation != myposition)
            {
                endNode = lastLocation;
                dijkstraSimple();
            }
            else
            {
                endNode = mygraph.find(0);
                dijkstraSimple();
            }
        }

        i = 0;
        distance = 10000;

        while(i < neighbours.size())
        {
            startNode = neighbours.get(i);
            tempdistance = dijkstraSimple();
            if(tempdistance < distance)
            {
                bestNode = startNode;
                distance = tempdistance;
            }

            i++;
        }

        return bestNode;
    }

    private Move ConvertDetNodeToMove(Node bestNode)
    {
        int i = 0;
        Move bestMove = null;
        List<Edge> edges = mygraph.edges(myposition.name().trim());
        Edge connectingEdge = null;
        int bestNodeid = Integer.parseInt(bestNode.name().trim());

        while(i < edges.size())
        {
            if((convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Taxi && taxiTickets < 1) || (convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Bus && busTickets < 1) || (convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Underground && undergroundTickets < 1))
            {
                //System.out.println("[PATHFINDING]: Removed edge connecting " + edges.get(i).id1() + " and " + edges.get(i).id2());
                edges.remove(i);
                i -= 1;
            }
            i++;
        }

        i = 0;

        if(edges.size() == 0)
        {
            edges = mygraph.edges(myposition.name().trim());
        }

        while(i < edges.size())
        {
            //System.out.println("[PATHFINDING]: Edge value " + i + ": " + edges.get(i).id1() + " connects " + edges.get(i).id2());
            if(edges.get(i).id1().equals(bestNode.name().trim()) || edges.get(i).id2().equals(bestNode.name().trim()))
            {
                connectingEdge = edges.get(i);
            }

            i++;
        }

        bestMove = new Move(convertEdgeTypeToTicketType(connectingEdge.type()), bestNodeid);
        return bestMove;
    }

    private List<Node> GetValidMoves(Node position, int plrid, int taxi, int bus, int underground)
    {
        List<Node> neighbours = new ArrayList<Node>();
        List<Edge> edges = mygraph.edges(position.name().trim());
        int i = 0;
        int j = 0;

        if(position == mrxposition && plrid == mrXId)
        {
            disablesecret = true;
        }

        while(i < edges.size())
        {
            if(position == mrxposition && plrid == mrXId)
            {
                //System.out.println("[PATHFINDING]: Edge type is " + convertEdgeTypeToTicketType(edges.get(i).type()));
                if(convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Bus || convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Underground)
                {
                    disablesecret = false;
                }
            }

            if((convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Taxi && taxi < 1) || (convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Bus && bus < 1) || (convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Underground && underground < 1))
            {
                edges.remove(i);
                i -= 1;
            }
            else
            {
                if(edges.get(i).id1().equals(position.name().trim()) && !neighbours.contains(position.name().trim()))
                {
                    neighbours.add(mygraph.find(Integer.parseInt(edges.get(i).id2()) - 1));
                    //System.out.println("[PATHFINDING]: Adding node (found id1)" + neighbours.get(i).name());
                }

                else if(edges.get(i).id2().equals(position.name().trim()) && !neighbours.contains(position.name().trim()))
                {
                    neighbours.add(mygraph.find(Integer.parseInt(edges.get(i).id1()) - 1));
                    //System.out.println("[PATHFINDING]: Adding node (found id2)" + neighbours.get(i).name());
                }
            }
            i++;
        }

        i = 0;

        while(i < neighbours.size())
        {
            int neighbourid = Integer.parseInt(neighbours.get(i).name().trim());
            j = 0;

            while(j < aiReadable.getDetectiveIdList().size())
            {
                //System.out.println("[PATHFINDING]: Checking player " + playerId + " position with neighbour id " + neighbourid + " and node id " + aiReadable.getNodeId(playerId));
                if(aiReadable.getDetectiveIdList().get(j) != plrid)
                {
                    if(neighbourid == aiReadable.getNodeId(aiReadable.getDetectiveIdList().get(j)))
                    {
                        //System.out.println("[PATHFINDING]: Occupied location detected. Removing node " + neighbours.get(i).name().trim());
                        neighbours.remove(i);
                        j = aiReadable.getDetectiveIdList().size();
                        i -= 1;
                    }
                }
                j++;
            }
            i++;
        }

        if(plrid == mrXId){
            i = 0;
            while(i < neighbours.size()){
                //System.out.println("[PATHFINDING]: Mr X valid move is: " + neighbours.get(i).name().trim());
                i++;
            }
        }

        return neighbours;
    }

    private List<Node> RemoveDangerousNodes(List<Node> neighbours)
    {
        int i = 0;
        int j = 0;
        List<Node> detectiveNeighbours = new ArrayList<Node>();
        List<Node> sizeTest = new ArrayList<Node>();

        while (i < neighbours.size())
        {
            validMoves.add(neighbours.get(i));
            i++;
        }
        
        i = 0;
        
        while(i < neighbours.size())
        {
            sizeTest = findNeighbours(neighbours.get(i).name().trim());
            if(sizeTest.size() < 3)
            {
                neighbours.remove(i);
            }
            i++;
        }

        i = 0;
        j = 0;

        while(i < aiReadable.getDetectiveIdList().size())
        {
            int plrid = aiReadable.getDetectiveIdList().get(i);
            detectiveNeighbours = GetValidMoves(mygraph.find(aiReadable.getNodeId(plrid) - 1), plrid, aiReadable.getNumberOfTickets(Initialisable.TicketType.Taxi, plrid), aiReadable.getNumberOfTickets(Initialisable.TicketType.Bus, plrid), aiReadable.getNumberOfTickets(Initialisable.TicketType.Underground, plrid));
            j = 0;
            while(j < detectiveNeighbours.size())
            {
                if(neighbours.contains(detectiveNeighbours.get(j)))
                {
                    while(neighbours.contains(detectiveNeighbours.get(j)))
                    {
                        //System.out.println("[PATHFINDING]: Removing dangerous node " + detectiveNeighbours.get(j).name().trim());
                        neighbours.remove(detectiveNeighbours.get(j));
                    }
                }
                j++;
            }
            i++;
        }

        //Fallback incase no valid safe moves can be found
        if(dangerOverride == false || movessince == 1)
        {
            if(neighbours.size() == 0)
            {
                //System.out.println("[PATHFINDING]: No valid safe moves found. Resetting potential destination list.");
                if(movessince == 1)
                {
                    forcesecret = true;
                }
                i = 0;
                neighbours.clear();

                while(i < validMoves.size())
                {
                    neighbours.add(validMoves.get(i));
                    i++;
                }

                dangermode = 1;
            }
        }

        return neighbours;

    }

    private List<Node> GetMostNeighbours(List<Node> neighbours)
    {
        int i = 0;
        int j = 0;
        int mostneighbours = 0;
        List<Node> bestMoves = new ArrayList<Node>();

        while(i < neighbours.size())
        {
            List<Node> secondneighbours = findNeighbours(neighbours.get(i).name().trim());
            j = 0;
            while(j < secondneighbours.size())
            {
                int neighbourid = Integer.parseInt(secondneighbours.get(j).name().trim());

                for(int id: aiReadable.getDetectiveIdList())
                {
                    //System.out.println("[PATHFINDING]: Checking player " + id + " position with neighbour id " + neighbourid + " and node id " + aiReadable.getNodeId(id));
                    if(neighbourid == aiReadable.getNodeId(id))
                    {
                        secondneighbours.remove(j);
                        //System.out.println("[PATHFINDING]: Occupied location detected. Removing node from neighbours list.");
                        j -= 1;
                    }
                }
                j ++;
            }

            if(secondneighbours.size() - mostneighbours > 0)
            {
                bestMoves.clear();
                bestMoves.add(neighbours.get(i));
                //System.out.println("[PATHFINDING]: Found node with more neighbours: " + neighbours.get(i).name().trim());
                mostneighbours = secondneighbours.size();

            }
            else if (secondneighbours.size() == mostneighbours || secondneighbours.size() - mostneighbours == -1)
            {
                bestMoves.add(neighbours.get(i));
                //System.out.println("[PATHFINDING]: Found node with equal neighbours: " + neighbours.get(i).name().trim());
            }
            //System.out.println("[PATHFINDING]: Finding neighbours of neighbours of size " + secondneighbours.size());
            i++;
        }

        return bestMoves;
    }

    private Node GetTotalFurthestNeighbour(List<Node> bestMoves)
    {
        Node bestNode = null;
        int i = 0;
        int j = 0;
        double tempdistance = 0;
        double distance = 0;

        while(i < bestMoves.size())
        {
            startNode = bestMoves.get(i);
            tempdistance = 0;
            j = 0;

            while(j < aiReadable.getDetectiveIdList().size())
            {
                endNode = mygraph.find(aiReadable.getNodeId(aiReadable.getDetectiveIdList().get(j)) - 1);
                tempdistance += dijkstraSimple();
                j++;
            }

            //System.out.println("[PATHFINDING]: Best Move Node " + bestMoves.get(i).name().trim() + " distance from all detectives: " + tempdistance);

            if(tempdistance > distance)
            {
                distance = tempdistance;
                bestNode = bestMoves.get(i);
            }

            i++;
        }

        return bestNode;
    }

    private Move ConvertNodeToMove(Node bestNode)
    {
        Move bestMove = null;
        int bestNodeid = Integer.parseInt(bestNode.name().trim());
        int i = 0;
        int currentLocation = aiReadable.getNodeId(playerId);
        Edge connectingEdge = null;
        List<Edge> edges = mygraph.edges(myposition.name().trim());

        if(dangermode == 0 || ((aiReadable.getNumberOfTickets(Initialisable.TicketType.DoubleMove, playerId) == 0 && aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId) == 0)) || dangerOverride == true)
        {
            //System.out.println("[PATHFINDING]: No danger found. Use normal ticket.");
            if(dangerOverride == true)
            {
                if(movessince == 1)
                {
                    movessince = 0;
                    dangerOverride = false;
                }
                else
                {
                    movessince += 1;
                }
            }

            displacement += 1;

            while(i < edges.size())
            {
                if((convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Taxi && taxiTickets < 1) || (convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Bus && busTickets < 1) || (convertEdgeTypeToTicketType(edges.get(i).type()) == Initialisable.TicketType.Underground && undergroundTickets < 1))
                {
                    //System.out.println("[PATHFINDING]: Removed edge connecting " + edges.get(i).id1() + " and " + edges.get(i).id2());
                    edges.remove(i);
                    i -= 1;
                }
                i++;
            }

            i = 0;

            while(i < edges.size())
            {
                //System.out.println("[PATHFINDING]: Edge value " + i + ": " + edges.get(i).id1() + " connects " + edges.get(i).id2());
                if(edges.get(i).id1().equals(bestNode.name().trim()) || edges.get(i).id2().equals(bestNode.name().trim()))
                {
                    connectingEdge = edges.get(i);
                    //System.out.println("[PATHFINDING]: This is the edge I want, type: " + connectingEdge.type());
                }

                i++;
            }

            bestMove = new Move(convertEdgeTypeToTicketType(connectingEdge.type()), bestNodeid);
        }
        else
        {
            if(dangermode == 1)
            {
                if(aiReadable.getNumberOfTickets(Initialisable.TicketType.DoubleMove, playerId) > 0)
                {
                    //System.out.println("[PATHFINDING]:---------Danger found. Using Double Move");
                    bestMove = new Move(Initialisable.TicketType.DoubleMove, currentLocation);
                    dangerOverride = true;

                }

                else if(aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId) > 0)
                {
                    //System.out.println("[PATHFINDING]:------Danger found. Using secret move");
                    bestMove = new Move(Initialisable.TicketType.SecretMove, bestNodeid);
                }
            }
            else
            {
                bestMove = new Move(Initialisable.TicketType.SecretMove, bestNodeid);  
            }
        }

        if(forcesecret == true && aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId) > 0 && disablesecret == false)
        {
            forcesecret = false;
            bestMove.type = Initialisable.TicketType.SecretMove;
        }
        return bestMove;
    }

    private double dijkstraSimple()
    {
        //System.out.println("[PATHFINDING]: Running dijkstra on nodes " + startNode.name() + " and " + endNode.name());
        Graph g = mygraph;
        List<Node> unvisited = new ArrayList<Node>(g.nodes());
        List<Node> visited = new ArrayList<Node>();
        HashMap<Node, Node> route = new HashMap<Node, Node>();
        List<Node> neighbours = new ArrayList<Node>();

        for(Node n : unvisited)
        {
            dist.put(n.name(),Double.MAX_VALUE);
        }

        Node u = startNode;
        dist.put(u.name(),0.0);

        while(!unvisited.isEmpty())
        {
            visited.add(u);
            unvisited.remove(u);
            for(Node nb : findNeighbours(u.name().trim()))
            {
                if(!visited.contains(nb))
                {
                    if(dist.get(u.name())+1 < dist.get(nb.name()))
                    {
                        dist.put(nb.name(), dist.get(u.name())+1);
                        route.put(nb, u);
                    }
                }
            }

            int i = 0;

            u = minDistance(unvisited, dist);
            if(u.equals(endNode))
                break;
        }
        makeRoute(route, endNode);
        return dist.get(endNode.name());
    }

    private List<Node> findNeighbours(String n) {
        //System.out.println("Finding neighbours to node " + n.name());
        List<Node> output = new ArrayList<Node>();
        List<String> edgeoutput = new ArrayList<String>();

        List<Edge> edges = mygraph.edges(n);

        for(Edge e : edges) 
        {
            if(e.id1().equals(n))
            {
                if(!edgeoutput.contains(e.id2()))
                {
                    edgeoutput.add(e.id2());
                }
            }

            else
            {
                if(!edgeoutput.contains(e.id1()))
                {
                    edgeoutput.add(e.id1());
                }
            }

        }

        int i = 0;
        String edgename;
        int nodeid;

        while(i < edgeoutput.size())
        {
            edgename = edgeoutput.get(i);
            nodeid = Integer.parseInt(edgename) - 1;
            output.add(mygraph.find(nodeid));
            i++;
        }

        return output;
    }

    private void makeRoute(HashMap<Node, Node> nodes, Node targetNode)
    {
        List<Node> path = new ArrayList<Node>();
        Node cur = targetNode;
        while(nodes.get(cur) != null)
        {
            path.add(cur);
            cur = nodes.get(cur);
        }
        path.add(cur);
        Collections.reverse(path);
        shortestPath = path;
    }

    private Node minDistance(List<Node> ns, HashMap<String, Double> dist)
    {
        double minD = Double.MAX_VALUE;
        Node output = startNode;
        for(Node n : ns)
        {
            if(dist.get(n.name()) < minD)
            {
                minD = dist.get(n.name());
                output = n;
            }
        }

        return output;
    }
}
