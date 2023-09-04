/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Avion;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import Reservation.avionDB;
/**
 *
 * @author Santatra
 */
public class Recette extends javax.swing.JFrame {
    PreparedStatement pst=null;
    ResultSet rs=null;
    Connection con=null;
    private String numero="";
    private String designation="";
    /**
     * Creates new form Recette
     */
    public Recette(String num,String design) {
        initComponents();
        Num_form.setText(num);
        Design_form.setText(design);
        Num_form.setEnabled(false);
        Design_form.setEnabled(false);
        con=avionDB.ConnectDB();
        numero=num;
        designation=design;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    public void pdf(){
    String num=Num_form.getText();
    String design=Design_form.getText();
    String annee=date_form.getText();
    String total=Total.getText();
    JFileChooser dialog=new JFileChooser();
    String req="SELECT SUM(VOL.frais) AS RECETTE, STRFTIME('%m',RESERVATION.Date_Reservation) AS MOIS FROM VOL,AVION,RESERVATION WHERE (VOL.NumVol=AVION.NumVol) AND (AVION.NumAVION=RESERVATION.NumAvion) AND STRFTIME('%Y',RESERVATION.Date_Reservation)=='"+date_form.getText()+"'  AND AVION.NumAvion=='"+Num_form.getText()+"' GROUP BY STRFTIME('%m',RESERVATION.Date_Reservation) ORDER BY STRFTIME('%m',RESERVATION.Date_Reservation)";
    dialog.setSelectedFile(new File("santax.pdf"));
    int dialogResult=dialog.showSaveDialog(null);
    if(dialogResult==JFileChooser.APPROVE_OPTION){
        String filepath=dialog.getSelectedFile().getPath();
        
        try{
            Document myDocument=new Document();
            PdfWriter  myWriter=PdfWriter.getInstance(myDocument,new FileOutputStream(filepath));
            myDocument.open();
            
            myDocument.add(new Paragraph("N°AVION : "+num,FontFactory.getFont(FontFactory.TIMES_BOLD,15,Font.BOLD)));
            myDocument.add(new Paragraph("DESIGNATION : "+design,FontFactory.getFont(FontFactory.TIMES_BOLD,15,Font.BOLD)));
            myDocument.add(new Paragraph("ANNEE : "+annee,FontFactory.getFont(FontFactory.TIMES_BOLD,15,Font.BOLD)));
            myDocument.add(new Paragraph(" ",FontFactory.getFont(FontFactory.TIMES_BOLD,15,Font.BOLD)));
            PdfPTable table=new PdfPTable(2);
            table.setWidthPercentage(100);
            
            PdfPCell cell;
            cell=new PdfPCell(new Phrase("Recette",FontFactory.getFont("Comic Sans MS",12)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.DARK_GRAY);//GRAY
            table.addCell(cell);
            
            cell=new PdfPCell(new Phrase("Mois",FontFactory.getFont("Comic Sans MS",12)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.DARK_GRAY);//GRAY
            table.addCell(cell);
            
            /////////////////////////////////////////////////
            
            try{
                pst=con.prepareStatement(req);
                rs=pst.executeQuery();
                while(rs.next()){
                    cell=new PdfPCell(new Phrase(rs.getString("RECETTE")+"Ar",FontFactory.getFont("Arial",11)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
            
                    cell=new PdfPCell(new Phrase(mois_to_string(rs.getString("MOIS")),FontFactory.getFont("Arial",11)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, e);
            }
            
            myDocument.add(table);
            myDocument.add(new Paragraph(" ",FontFactory.getFont(FontFactory.TIMES_BOLD,15,Font.BOLD)));
            myDocument.add(new Paragraph("Total: "+total,FontFactory.getFont(FontFactory.TIMES_BOLD,15,Font.BOLD)));
            myDocument.close();
            JOptionPane.showMessageDialog(null, "le document est bien genéré");
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    }
    }
    public void recette(String annee,String avion){
        double somme=0;
        String req="SELECT SUM(VOL.frais) AS RECETTE, STRFTIME('%m',RESERVATION.Date_Reservation) AS MOIS FROM VOL,AVION,RESERVATION WHERE (VOL.NumVol=AVION.NumVol) AND (AVION.NumAVION=RESERVATION.NumAvion) AND STRFTIME('%Y',RESERVATION.Date_Reservation)=='"+annee+"'  AND AVION.NumAvion=='"+avion+"' GROUP BY STRFTIME('%m',RESERVATION.Date_Reservation) ORDER BY STRFTIME('%m',RESERVATION.Date_Reservation)";
        try{
            pst=con.prepareStatement(req);
            rs=pst.executeQuery();
            //int i=1;
            DefaultTableModel model=new DefaultTableModel(new String[]{
               "RECETTE","MOIS"
            },0){
                public boolean isCellEditable(int row ,int column){
                    return false;
                }
            };
            while(rs.next()){
                somme+=rs.getDouble("RECETTE");
                model.addRow(new Object[]{
                    rs.getString("RECETTE")+"Ar",
                    mois_to_string(rs.getString("MOIS"))
                });
            }
            pst.close();
            rs.close();
            recette_tbl.setModel(model);
            recette_tbl.setRowHeight(25);
            Total.setText(somme+" Ar");
            Total.setEnabled(false);
            //JOptionPane.showMessageDialog(null, String.valueOf(somme));
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    public String mois_to_string(String nb){
        String ret="";
        switch(nb){
            case "01":
                ret="Janvier";
                break;
            case "02":
                ret="Fevrier";
                break;
            case "03":
                ret="Mars";
                break;
            case "04":
                ret="Avril";
                break;
            case "05":
                ret="Mai";
                break;
            case "06":
                ret="Juin";
                break;
            case "07":
                ret="Juillet";
                break;
            case "08":
                ret="Août";
                break;
            case "09":
                ret="Septembre";
                break;
            case "10":
                ret="Octobre";
                break;
            case "11":
                ret="Novembre";
                break;
            case "12":
                ret="Décembre";
                break;
            default :
                ret="Sampo agn";
        }
        return ret;
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        recette_tbl = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        date_form = new javax.swing.JTextField();
        num = new javax.swing.JLabel();
        design = new javax.swing.JLabel();
        Num_form = new javax.swing.JTextField();
        Design_form = new javax.swing.JTextField();
        Total = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        pdf_btn = new javax.swing.JButton();
        fermer = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        recette_tbl.setBackground(new java.awt.Color(0, 204, 51));
        recette_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(recette_tbl);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 660, 320));

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setText("RECETTE");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 70, 270, 40));

        jLabel2.setFont(new java.awt.Font("Lucida Handwriting", 0, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("AIR MAD");
        jLabel2.setToolTipText("");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, 280, 40));

        date_form.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                date_formActionPerformed(evt);
            }
        });
        date_form.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                date_formKeyReleased(evt);
            }
        });
        jPanel1.add(date_form, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 140, 140, 30));

        num.setText("N°Avion: ");
        jPanel1.add(num, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        design.setText("designation");
        jPanel1.add(design, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 140, -1, -1));
        jPanel1.add(Num_form, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 140, 80, -1));
        jPanel1.add(Design_form, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 140, 100, -1));
        jPanel1.add(Total, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 510, 130, -1));

        jLabel3.setText("Total:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 510, -1, -1));

        pdf_btn.setText("PDF");
        pdf_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdf_btnActionPerformed(evt);
            }
        });
        jPanel1.add(pdf_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 510, -1, -1));

        fermer.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        fermer.setForeground(new java.awt.Color(255, 0, 0));
        fermer.setText("X");
        fermer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fermerMouseClicked(evt);
            }
        });
        jPanel1.add(fermer, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 40, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(686, 574));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void date_formActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_date_formActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_date_formActionPerformed

    private void date_formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_date_formKeyReleased
        // TODO add your handling code here:
        recette(date_form.getText(),numero);
    }//GEN-LAST:event_date_formKeyReleased

    private void pdf_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdf_btnActionPerformed
        pdf();
    }//GEN-LAST:event_pdf_btnActionPerformed

    private void fermerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fermerMouseClicked
        dispose();
    }//GEN-LAST:event_fermerMouseClicked

    /**
     * @param args the command line arguments
     */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Design_form;
    private javax.swing.JTextField Num_form;
    private javax.swing.JTextField Total;
    private javax.swing.JTextField date_form;
    private javax.swing.JLabel design;
    private javax.swing.JLabel fermer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel num;
    private javax.swing.JButton pdf_btn;
    private javax.swing.JTable recette_tbl;
    // End of variables declaration//GEN-END:variables
}
