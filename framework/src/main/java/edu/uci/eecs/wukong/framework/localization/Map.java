package edu.uci.eecs.wukong.framework.localization;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Map {
    private BufferedImage image;
    private boolean[][] map;
    private double[][] dijkstramap;

    public Map(File f, int tx, int ty) throws IOException {
        image= ImageIO.read(f);
        map=new boolean[image.getWidth()][image.getHeight()];
        for (int x=0;x<image.getWidth();x++){
            for (int y=0;y<image.getHeight();y++){
                if (image.getRGB(x,y)==-1){
                    map[x][y]=true;
                }
                else{
                    map[x][y]=false;

                }
            }
        }
        createDijkstraMap(tx,ty);
    }

    public Map(boolean[][] map, int tx, int ty){
        this.map=map;
        createDijkstraMap(tx,ty);
    }

    public int getWidth(){return map.length;}
    public int getHeight(){return map[0].length;}
    public boolean getData(int x, int y){return map[x][y];}

    public double getObservationProbability(double x, double y, double a, double[] sensors, double[] sensorvalues, double noise, int maxr){
        double[] real=getSensorValues(x,y,a,sensors,maxr);
        double p=1;
        for (int i=0;i<sensors.length;i++){
                p*=NormalDistribution.getProbability(real[i],noise,sensorvalues[i]);
        }
        return p;
    }

    public double[][] getDijkstraMap(){
        return dijkstramap;
    }
    public double getDirection(int ax,int ay, double aa){
        int minx=0;
        double minval=Double.MAX_VALUE;
        for (int x=0;x<8;x++){
            int tx=ax;
            int ty=ay;
            switch(x){
                case 0:
                    ty-=10;
                    break;
                case 1:
                    tx+=10;
                    ty-=10;
                    break;
                case 2:
                    tx+=10;
                    break;
                case 3:
                    tx+=10;
                    ty+=10;
                    break;
                case 4:
                    ty+=10;
                    break;
                case 5:
                    tx-=10;
                    ty+=10;
                    break;
                case 6:
                    tx-=10;
                    break;
                case 7:
                    tx-=10;
                    ty-=10;
                    break;
            }
            if (tx>=0&&ty>=0&&tx<dijkstramap.length&&ty<dijkstramap[x].length&&dijkstramap[tx][ty]<minval&&openPoint(tx,ty,5)){
                minval=dijkstramap[tx][ty];
                minx=x;
            }
        }
        double angle=minx*Math.PI/4;
        double val=angle-aa;
        while(val<0)val+=2*Math.PI;
        while(val>2*Math.PI)val-=2*Math.PI;
        if (val>Math.PI)val=-(2*Math.PI-val);
        return val;
    }

    private double[] getSensorValues(double xpos, double ypos,double angle, double[] sensors, int maxr){
        double vals[]=new double[sensors.length];
        for (int x=0;x<sensors.length;x++){
            vals[x]=getDistance(xpos,ypos,angle+sensors[x], maxr);
        }
        return vals;
    }

    private double getDistance(double xpos, double ypos,double angle, int maxr){
        boolean detected=false;
        double r=1;
        while(!detected){
            r+=0.5;
            int x=(int)Math.round(xpos+r*Math.sin(angle));
            int y=(int)Math.round(ypos-r*Math.cos(angle));
            if (x<0||x>getWidth()-1||y<0||y>getHeight()-1)detected=true;
            else if (!map[x][y])detected=true;
            if (maxr>0&&r>=10*maxr)detected=true;
        }
        return r;
    }

    private void createDijkstraMap(int tx, int ty){
        dijkstramap=new double[map.length][map[0].length];
        for (int x=0;x<map.length;x++){
            for (int y=0;y<map[x].length;y++){
                //if (!map[x][y])dijkstramap[x][y]=-1;
                dijkstramap[x][y]=Double.MAX_VALUE;
            }
        }
        dijkstramap[tx][ty]=0;

        dijkstra(dijkstramap,tx,ty);
    }

    class Point{
        private int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            if (y != point.y) return false;

            return true;
        }

    }

    private void dijkstra(double[][] dijkstramap, int tx, int ty){
        ArrayList<Point> queue=new ArrayList<Point>();
        queue.add(new Point(tx,ty));
        while(!queue.isEmpty()){
            Point p=queue.remove(0);
            int x=p.getX();
            int y=p.getY();

            double val=dijkstramap[x][y];
            if (x>0&&map[x-1][y]){
                double tval=dijkstramap[x-1][y]+1;
                if (tval<val)val=tval;
            }
            if (x<map.length-1&&map[x+1][y]){
                double tval=dijkstramap[x+1][y]+1;
                if (tval<val)val=tval;
            }
            if (y>0&&map[x][y-1]){
                double tval=dijkstramap[x][y-1]+1;
                if (tval<val)val=tval;
            }
            if (y<map[x].length-1&&map[x][y+1]){
                double tval=dijkstramap[x][y+1]+1;
                if (tval<val)val=tval;
            }
            /*if (x>0&&y>0&&map[x-1][y-1]){
                double tval=dijkstramap[x-1][y-1]+Math.sqrt(2);
                if (tval<val)val=tval;
            }
            if (x<map.length-1&&y>0&&map[x+1][y-1]){
                double tval=dijkstramap[x+1][y-1]+Math.sqrt(2);
                if (tval<val)val=tval;
            }
            if (x<map.length-1&&y<map[x].length&&map[x+1][y+1]){
                double tval=dijkstramap[x+1][y+1]+Math.sqrt(2);
                if (tval<val)val=tval;
            }
            if (x>0&&y<map[x].length&&map[x-1][y+1]){
                double tval=dijkstramap[x-1][y+1]+Math.sqrt(2);
                if (tval<val)val=tval;
            } */
            //System.out.println(val+" < "+dijkstramap[x][y]);
            if (dijkstramap[x][y]==0||Math.round(10*dijkstramap[x][y])>Math.round(10*val)){
                dijkstramap[x][y]=val;
                //System.out.println(x+" "+y+" "+dijkstramap[x][y]+" "+queue.size());
                if (x>0&&dijkstramap[x][y]+1<dijkstramap[x-1][y]&&map[x-1][y])queue.add(new Point(x-1,y));
                if (x<map.length-1&&dijkstramap[x][y]+1<dijkstramap[x+1][y]&&map[x+1][y])queue.add(new Point(x+1,y));
                if (y>0&&dijkstramap[x][y]+1<dijkstramap[x][y-1]&&map[x][y-1])queue.add(new Point(x,y-1));
                if (y<map[x].length-1&&dijkstramap[x][y]+1<dijkstramap[x][y+1]&&map[x][y+1])queue.add(new Point(x,y+1));
                /*if (x>0&&y>0&&dijkstramap[x][y]+Math.sqrt(2)<dijkstramap[x-1][y-1]&&map[x-1][y-1])queue.add(new Point(x-1,y-1));
                if (x<map.length-1&&y>0&&dijkstramap[x][y]+Math.sqrt(2)<dijkstramap[x+1][y-1]&&map[x+1][y-1])queue.add(new Point(x+1,y-1));
                if (x<map.length-1&&y<map[x].length&&dijkstramap[x][y]+Math.sqrt(2)<dijkstramap[x+1][y+1]&&map[x+1][y+1])queue.add(new Point(x+1,y+1));
                if (x>0&&y<map[x].length&&dijkstramap[x][y]+Math.sqrt(2)<dijkstramap[x-1][y+1]&&map[x-1][y+1])queue.add(new Point(x-1,y+1));*/
            }
        }
    }

    private boolean openPoint(int x, int y, int d){
        if (x+d<map.length&&y+d<map[0].length&&!map[x+d][y+d])return false;
        if (x-d>=0&&y+d<map[0].length&&!map[x-d][y+d])return false;
        if (x-d>=0&&y-d>=0&&!map[x-d][y-d])return false;
        if (x+d<map.length&&y-d>=0&&!map[x+d][y-d])return false;
        return true;
    }

}
