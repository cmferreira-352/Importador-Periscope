package br.ufmt.importador.periscope;

import br.ufmt.importador.periscope.controller.ApplicantJpaController;
import br.ufmt.importador.periscope.controller.ClassificationJpaController;
import br.ufmt.importador.periscope.controller.CountryJpaController;
import br.ufmt.importador.periscope.controller.InventorJpaController;
import br.ufmt.importador.periscope.model.Applicant;
import br.ufmt.importador.periscope.model.Classification;
import br.ufmt.importador.periscope.model.Inventor;
import br.ufmt.importador.periscope.model.Patent;
import br.ufmt.importador.periscope.model.Priority;
import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.mapping.lazy.DatastoreHolder;
import com.mongodb.Mongo;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Hello world!
 *
 */
public class Main {

//    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("periscopePU");
//    static CountryJpaController countrycontroller = new CountryJpaController(emf);
//
//    private static void initCountry() {
//        System.out.println("antes do parse");
//        List<Country> countries = Fixjure.listOf(Country.class).from(YamlSource.newYamlResource("country-inicial-data.yaml")).create();
//        System.out.println("depois do parse");
//        Iterator<Country> it = countries.iterator();
//        while (it.hasNext()) {
//            countrycontroller.create(it.next());
//            System.out.println("salvando...");
//        }
//    }
    public static void main(String[] args) {
        long tempopostgres = 0;
        long tempomongo = 0;
//        initCountry();
        Datastore ds = null;
        try {
            Mongo mongo = new Mongo();
            Morphia morphia = new Morphia();
            ds = morphia.createDatastore(mongo, "PeriscopeEri");
            ds.ensureCaps();
            ds.ensureIndexes();
            morphia.mapPackage("br.ufmt.importador.periscope.model");
            DatastoreHolder.getInstance().set(ds);
        } catch (Exception e) {
            System.out.println("Não conectou o Mongo!");
            e.printStackTrace();
        }
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("periscopePU");
//        Importador importador = new Importador(emf);
//        PatentJpaController patentJpaController = new PatentJpaController(emf);
        File dir = new File("/home/cristhian/Dropbox/Computação/Projeto/Periscope/Arquivos de Teste/Importação/ESPACENET/Patentes");
        File[] xls = dir.listFiles();
        Random r = new Random(123);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        CountryJpaController countryJpaController;
        countryJpaController = new CountryJpaController(emf);
//        InventorJpaController inventorJpaController = new InventorJpaController(emf);
//        ApplicantJpaController applicantJpaController = new ApplicantJpaController(emf);
//        ClassificationJpaController classificationJpaController = new ClassificationJpaController(emf);
        List<Patent> cache = new ArrayList<Patent>();
        for (int i = 0; i < 100000; i++) {
//            System.out.println("Import:"+i);
            Patent patent = new Patent();
            patent.setTitleSelect("Title " + r.nextInt());
            patent.setPublicationNumber("BR000" + r.nextInt(10) + "" + r.nextInt(10) + "" + r.nextInt(10) + "" + r.nextInt(10) + " (A)");
            patent.setApplicationNumber("BR2000000" + r.nextInt(10) + "" + r.nextInt(10) + "" + r.nextInt(10) + "" + r.nextInt(10));
            String data = r.nextInt(3) + "" + r.nextInt(10) + "/" + r.nextInt(2) + r.nextInt(10) + "/20" + r.nextInt(10) + "" + r.nextInt(10);

            Priority prio = new Priority();
            prio.setValue("2000000" + r.nextInt(10) + "" + r.nextInt(10) + "" + r.nextInt(10) + "" + r.nextInt(10));
            prio.setIdCountry(countryJpaController.findCountryByAcronym("BR"));
//            prio.setIdPatent(patent);

            try {
                patent.setPublicationDate(sdf.parse(data));
                patent.setApplicantionDate(sdf.parse(data));
                prio.setDate(sdf.parse(data));
            } catch (ParseException ex) {
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            patent.getPriorityCollection().add(prio);

            Applicant app = new Applicant();
            app.setName("Applicant" + r.nextInt() % 10000);
            if (!app.getName().trim().isEmpty()) {
                app.setName(app.getName());
//                Applicant existente = applicantJpaController.findByName(app.getName());
//                if (existente != null) {
//                    app = existente;
//                }

            }
            app.setIdCountry(countryJpaController.findCountryByAcronym("BR"));
            patent.getApplicantCollection().add(app);

            Inventor inv = new Inventor();
            inv.setNome("Inventor" + r.nextInt() % 10000);
//            Inventor existente = inventorJpaController.findByName(inv.getNome());
//            if (existente != null) {
//
//                inv = existente;
//            }
            inv.setIdCountry(countryJpaController.findCountryByAcronym("BR"));
            patent.getInventorCollection().add(inv);

            Classification cl = new Classification();
            String l = ((char) (r.nextInt(8) + 65)) + "";
//            System.out.println(l);
            data = l + r.nextInt(10) + "" + r.nextInt(10) + "K" + r.nextInt(10) + "" + r.nextInt(10) + "/" + r.nextInt(10) + "" + r.nextInt(10) + "";
            cl.setValue(data);
            cl.setKlass(data.substring(0, 4));
            cl.setGroup(Short.parseShort(data.substring(4, data.indexOf("/"))));
            cl.setSubgroup(Short.parseShort(data.substring(data.indexOf("/") + 1)));
            cl.setType("IC");
//            System.out.println(data);
//            cl.setValue(data);
//            Classification clexist = classificationJpaController.findByName(cl.getValue());
//            if (clexist != null) {
//                cl = clexist;
//            }
            patent.getClassificationCollection().add(cl);
            patent.setIdMainClassification(cl);



//            List<Applicant> lista = (List<Applicant>) patent.getApplicantCollection();
//            for (Applicant applicant : lista) {
//                System.out.println(applicant.getName());
//                applicant.getPatentCollection().add(patent);
////                System.out.println(applicant.getIdCountry().getName());
//            }
//            List<Inventor> listaInventor = (List<Inventor>) patent.getInventorCollection();
//            for (Inventor inventor : listaInventor) {
//                inventor.getPatentCollection().add(patent);
//            }
//            List<Classification> listaClassification = (List<Classification>) patent.getClassificationCollection();
//            for (Classification classification : listaClassification) {
//                classification.getPatentCollection().add(patent);
//            }
//            List<Priority> listaPrioridades = (List<Priority>) patent.getPriorityCollection();
//            for (Priority priority : listaPrioridades) {
//                priority.setIdPatent(patent);
//            }
            //TEMPO
//            long inicialPostgre = System.currentTimeMillis();
//            EntityManager em = emf.createEntityManager();
//            em.getTransaction().begin();
//            patent = em.merge(patent);
//
//            for (Applicant applicantCollectionApplicant : patent.getApplicantCollection()) {
//                applicantCollectionApplicant.getPatentCollection().add(patent);
//                applicantCollectionApplicant = em.merge(applicantCollectionApplicant);
//            }
//            for (Inventor inventorCollectionInventor : patent.getInventorCollection()) {
//                inventorCollectionInventor.getPatentCollection().add(patent);
//                inventorCollectionInventor = em.merge(inventorCollectionInventor);
//            }
//            for (Classification classificationCollectionClassification : patent.getClassificationCollection()) {
//                classificationCollectionClassification.getPatentCollection().add(patent);
//                classificationCollectionClassification = em.merge(classificationCollectionClassification);
//            }
//
//            em.getTransaction().commit();
////                    System.out.println("Persistiu");
//            em.close();
//            tempopostgres += System.currentTimeMillis() - inicialPostgre;
            //TEMPO POSTGRES


            //TEMPO MONGO
            
                long inicialMongo = System.currentTimeMillis();
                ds.save(patent);
                tempomongo += System.currentTimeMillis()-inicialMongo;
            
            

            //TEMPO MONGO





        }
        System.out.println("Tempo total posgre: " + tempopostgres);
        System.out.println("Tempo total mongo: " + tempomongo);
//        System.out.println("Hello World!");
    }
}
