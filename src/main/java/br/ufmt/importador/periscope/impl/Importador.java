/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufmt.importador.periscope.impl;

import br.ufmt.importador.periscope.controller.ApplicantJpaController;
import br.ufmt.importador.periscope.controller.ClassificationJpaController;
import br.ufmt.importador.periscope.controller.CountryJpaController;
import br.ufmt.importador.periscope.controller.InventorJpaController;
import br.ufmt.importador.periscope.model.Applicant;
import br.ufmt.importador.periscope.model.Classification;
import br.ufmt.importador.periscope.model.Country;
import br.ufmt.importador.periscope.model.Inventor;
import br.ufmt.importador.periscope.model.Patent;
import br.ufmt.importador.periscope.model.Priority;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Importador {

    public Importador(EntityManagerFactory emf) {
//        setEmf(emf);
        countryJpaController = new CountryJpaController(emf);
        inventorJpaController = new InventorJpaController(emf);
        applicantJpaController = new ApplicantJpaController(emf);
        classificationJpaController = new ClassificationJpaController(emf);
    }
//    private EntityManagerFactory emf;
    private Patent patent = new Patent();
    private Inventor inventor = new Inventor();
    private CountryJpaController countryJpaController;
    private InventorJpaController inventorJpaController;
    private ApplicantJpaController applicantJpaController;
    private ClassificationJpaController classificationJpaController;
    private String lang = "EN";
    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private Row row;
    private Iterator<Row> rowIterator;
    private InputStream content;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    public void cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Fake code simulating the copy
            // You can generally do better with nio if you need...
            // And please, unlike me, do something about the Exceptions :D
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();

            // Open new InputStreams using the recorded bytes
            // Can be repeated as many times as you wish

            content = new ByteArrayInputStream(baos.toByteArray());

        } catch (IOException ex) {
            Logger.getLogger(Importador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean hasNext() {

        if (rowIterator.hasNext()) {
            return true;
        }
        return false;
    }

    public Patent next() {
        patent = new Patent();
        parseLineXLS();

        return patent;
    }

    public void xlsManipulator() {
        try {
            wb = new HSSFWorkbook(content);
            sheet = wb.getSheetAt(0);

            rowIterator = sheet.iterator();

            //Pulando primeiras linhas
            rowIterator.next(); //Logotipo
            rowIterator.next();// Numero de resultados encontrados na busca
            rowIterator.next(); // Titulo da pesquisa
            rowIterator.next(); // Quantidade de Publicações exibidas
            rowIterator.next(); // Nome das Colunas (Titulo, Publicação, Autor ...)
        } catch (IOException ex) {
            Logger.getLogger(Importador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void parseLineXLS() {

        String contentString;

        patent.setLanguage(lang);

        row = rowIterator.next(); //Percorrendo cada linha (patente)
//        System.out.println(row.toString());
        // Para cada linha (patente), pega cada atributo (Titulo, Publicação, Autor ...)
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {

            Cell cell = cellIterator.next(); // Pegando cada coluna (atributo)

            switch (cell.getCellType()) {

                case Cell.CELL_TYPE_STRING:
                    contentString = cell.getStringCellValue().replaceAll("[\u2002]", " "); // Isso é para substituir o caracter especial por espaço em codificaçao UTF8
                    fillPatentXLS(cell.getColumnIndex(), contentString.replaceAll("[\u00e2][\u20ac][\u201a]", " ")); // // Isso é para substituir o caracter especial por espaço em codificaçao CP1252
                    break;

                default:
                    break;
            }

        }
    }

    public void fillPatentXLS(int columnIndex, String contentString) {
        String aux;
        StringTokenizer st;
//        EntityManager em = emf.createEntityManager();
//        em.getTransaction().begin();

        switch (columnIndex) {
            case 0:
                patent.setTitleSelect(contentString);
                break;
            case 1:
                patent.setPublicationNumber(contentString);
                break;
            case 2:
                try {
                    patent.setPublicationDate(sdf.parse(contentString));
                } catch (ParseException ex) {
                    Logger.getLogger(Importador.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 3:
                List<Inventor> inventors = (List<Inventor>) patent.getInventorCollection();
                st = new StringTokenizer(contentString, "\n");
                while (st.hasMoreTokens()) {
                    StringTokenizer st2 = new StringTokenizer(st.nextToken());
                    Inventor inventor = new Inventor();
                    String name = new String();
                    String country = new String();
                    while (st2.hasMoreTokens()) {
                        aux = st2.nextToken();
                        if (aux.matches("\\[.+\\]+")) {
                            aux = aux.replace("[", "");
                            aux = aux.replace("]", "");
                            country = aux.trim();
                        } else {
                            name = name.concat(aux + " ");
                        }

//                        System.out.println("Passou por ali");
                    }
                    if (!name.trim().isEmpty()) {

                        inventor.setNome(name);
                        Inventor existente = inventorJpaController.findByName(name);
                        if (existente != null) {

                            inventor = existente;
                        }
//                        inventor.getPatentCollection().add(patent);
//                        System.out.println("TÁ AQUI");
                        inventor.setIdCountry(countryJpaController.findCountryByAcronym(country));
//                        em.merge(inventor);
//                        em.getTransaction().commit();

                        inventors.add(inventor);
                    }
                }
                patent.setInventorCollection(inventors);
                break;
            case 4:
                List<Applicant> applicants = (List<Applicant>) patent.getApplicantCollection();
//                System.out.println("tam:" + applicants.size());
                st = new StringTokenizer(contentString, "\n");
                while (st.hasMoreTokens()) {
                    StringTokenizer st2 = new StringTokenizer(st.nextToken());
                    Applicant applicant = new Applicant();
                    String name = new String();
                    String country = new String();
                    while (st2.hasMoreTokens()) {
                        aux = st2.nextToken();
                        if (aux.matches("\\[.+\\]+")) {
                            aux = aux.replace("[", "");
                            aux = aux.replace("]", "");
                            country = aux.trim();
                        } else {
                            name = name.concat(aux + " ");
                        }

                    }
                    if (!name.trim().isEmpty()) {
                        applicant.setName(name);
                        Applicant existente = applicantJpaController.findByName(name);
                        if (existente != null) {
                            applicant = existente;
                        }
//                        applicant.getPatentCollection().add(patent);
                        applicant.setIdCountry(countryJpaController.findCountryByAcronym(country));
//                        em.merge(applicant);
                        applicants.add(applicant);

                    }
                }
                patent.setApplicantCollection(applicants);
                break;
            case 5:
                List<Classification> classifications = (List<Classification>) patent.getClassificationCollection();
                st = new StringTokenizer(contentString, "\n");
                String classe;
                boolean tem;
                while (st.hasMoreTokens()) {
                    classe = st.nextToken();
                    tem = false;
                    for (Classification classification : classifications) {
                        if (classification.getValue().equals(classe)) {
                            tem = true;
                        }
                    }
                    if (!tem) {
                        Classification classification = new Classification();
                        classification.setType("IC");
                        classification.setValue(classe);
//                    System.out.println("INSERINDO------------------------------------------------:"+classification.getValue());
                        Classification existente = classificationJpaController.findByName(classification.getValue());
                        if (existente != null) {
                            classification = existente;
                        }
//                        classification.getPatentCollection().add(patent);
                        classifications.add(classification);
                    }
                }
                try {
                    patent.setIdMainClassification(classifications.get(0));
                } catch (Exception ex) {
                }
                patent.setClassificationCollection(classifications);
                break;
            case 6:
                //"Cooperative Patent Classification: ";
                break;
            case 7:
                patent.setApplicationNumber(contentString);
                break;
            case 8:
                try {
                    patent.setApplicantionDate(sdf2.parse(contentString));
                } catch (ParseException ex) {
                    Logger.getLogger(Importador.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 9:
                List<Priority> priorities = (List<Priority>) patent.getPriorityCollection();
                st = new StringTokenizer(contentString, "\n");
                while (st.hasMoreTokens()) {
                    aux = st.nextToken();
                    Priority priority = new Priority();
                    Country c = countryJpaController.findCountryByAcronym(aux.substring(0, 2));
//                    System.out.println(c.getIdCountrsy());
    
                    priority.setIdCountry(countryJpaController.findCountryByAcronym(aux.substring(0, 2)));
                    StringTokenizer st2 = new StringTokenizer(aux);
                    aux = st2.nextToken();
                    priority.setValue(aux.substring(2));
                    aux = st2.nextToken();
                    try {
                        priority.setDate(sdf2.parse(aux));
                    } catch (ParseException ex) {
                        Logger.getLogger(Importador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    priority.setIdPatent(patent);
//                    em.merge(priority);
                    priorities.add(priority);
                }
                patent.setPriorityCollection(priorities);
                //"Priority number(s): ";
                //       pat.setPriorities(null);
                break;
        }
//        em.getTransaction().commit();
//        em.close();


    }
//
//    public EntityManagerFactory getEmf() {
//        return emf;
//    }
//
//    public void setEmf(EntityManagerFactory emf) {
//        this.emf = emf;
//    }
}
