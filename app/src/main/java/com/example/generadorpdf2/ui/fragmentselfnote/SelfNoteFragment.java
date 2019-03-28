package com.example.generadorpdf2.ui.fragmentselfnote;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.generadorpdf2.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.MarkedObject;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;





public class SelfNoteFragment extends Fragment implements View.OnClickListener {

    //------------------Declarando Variables-----------------------------------------
    private View mRootView;
    private EditText mSubjectEditText, mBodyEditText;
    private Button mSaveButton, mSendButton;
    private File myFile;
    private String NombreEmpresa = "CyberPunk";
    private String Direccion = "Calle Falsa #123";



    //Vacio
    public SelfNoteFragment() throws FileNotFoundException {
        // Required empty public constructor
    }
    public static SelfNoteFragment newInstance() throws FileNotFoundException {
        SelfNoteFragment fragment = new SelfNoteFragment();
        return fragment;
    }

    //------------------------Creacion de la activity----------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_self_note, container, false);
        mSubjectEditText = (EditText) mRootView.findViewById(R.id.edit_text_subject);
        mBodyEditText = (EditText) mRootView.findViewById(R.id.edit_text_body);
        mSaveButton = (Button) mRootView.findViewById(R.id.button_save);
        mSendButton = (Button) mRootView.findViewById(R.id.button_send);

        //Boton de guardar
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubjectEditText.getText().toString().isEmpty()){
                    mSubjectEditText.setError("El Campo esta vacio");
                    mSubjectEditText.requestFocus();
                    return;
                }
                if (mBodyEditText.getText().toString().isEmpty()){
                    mBodyEditText.setError("El Campo esta vacio");
                    mBodyEditText.requestFocus();
                    return;
                }

                try {

                    createPdf();
                    //Avisar que el PDF fue creado
                    Toast pdf_creado = Toast.makeText(getActivity(),"PDF Creado",Toast.LENGTH_SHORT);
                    pdf_creado.show();
                    Limpiar();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//Cierre el OnClick
            });

        //Boton de Enviar
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailNote();
            }
        });
        return mRootView;
    }//Cierra El onCreate



    private void createPdf() throws IOException, DocumentException, TransformerConfigurationException {

        //Crea la carpeta
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "OT_" + NombreEmpresa + "_" + Direccion + "_");

        //-------------asigna Fecha y Hora------------------
        Date date = new Date() ;
        //Capturando Fecha y Hora
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        //Creando el archivo
        File myFile = new File(pdfFolder + timeStamp + ".pdf");


        OutputStream output = new FileOutputStream(myFile);
        //Formato del PDF
        Document document = new Document(PageSize.LETTER);


        PdfWriter pdfW = PdfWriter.getInstance(document, new FileOutputStream(pdfFolder));
        PdfWriter.getInstance(document, output);

        //Abriendo el documento uy cargando datos
        document.open();
        document.addAuthor("AntiMouse");
        //document.add(new Paragraph(mSubjectEditText.getText().toString()));
        //document.add(new Paragraph(mBodyEditText.getText().toString()));

        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
        String htmlToPdf = "<html>" +
                "<head>" +
                "</head>" +
                "<body bgcolor='#E6E6FA'>" +
                "<p>" + "Este es un PDF de prueba"+"</p>" +

                "<h2> Nombre Empresa: "+ mBodyEditText.getText().toString() + "</h2>" +
                "</body> " +
                "<h1>"+ mSubjectEditText.getText().toString() + "</h1> " +
                "</html> ";

        String XHTML ="";


        worker.parseXHtml(pdfW, document, new StringReader(htmlToPdf));
        document.close();

        //viewPdf(myFile, getActivity());

    }

    private void viewPdf(File myFile, Context context){
        Toast.makeText(context,"Leyendo el archivo",Toast.LENGTH_SHORT).show();
        File file = new File(String.valueOf(myFile));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try{
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(context,"No tiene una app para abrir PDF", Toast.LENGTH_SHORT).show();
        }
        //startActivity(intent);
    }

    private void emailNote() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT,mSubjectEditText.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, mBodyEditText.getText().toString());
        Uri uri = Uri.parse(myFile.getAbsolutePath());
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);
    }

    private void Limpiar(){
        mSubjectEditText.setText("");
        mBodyEditText.setText("");
    }


    @Override
    public void onClick(View v) {
        return;
    }
}
