package com.example.generadorpdf2.ui.fragmentselfnote;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.generadorpdf2.R;
import com.itextpdf.text.Annotation;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.TransformerConfigurationException;


public class SelfNoteFragment extends Fragment implements View.OnClickListener {

    //------------------Declarando Variables-----------------------------------------
    private View mRootView;
    private EditText txtNombreEmpresa, txtDireccionEmpresa;
    private Button btnGuardar, btnEnviar;
    private RadioGroup RGroup;
    private RadioButton RadioServicio, RadioControl;
    private File myFile;
    private String NombreEmpresa = "CyberPunk";
    private String Direccion = "Calle Falsa #123";

    private String TipoServicio = "";



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
        txtNombreEmpresa = (EditText) mRootView.findViewById(R.id.EditTextNombreEmpresa);
        txtDireccionEmpresa = (EditText) mRootView.findViewById(R.id.EditTextDireccionEmpresa);
        btnGuardar = (Button) mRootView.findViewById(R.id.button_save);
        btnEnviar = (Button) mRootView.findViewById(R.id.button_send);
        RGroup = (RadioGroup)mRootView.findViewById(R.id.RGroup);
        RadioServicio = (RadioButton) mRootView.findViewById(R.id.RServicio);
        RadioControl = (RadioButton) mRootView.findViewById(R.id.RControl);

        //Boton de guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNombreEmpresa.getText().toString().isEmpty()){
                    txtNombreEmpresa.setError("El Campo esta vacio");
                    txtNombreEmpresa.requestFocus();
                    return;
                }
                if (txtDireccionEmpresa.getText().toString().isEmpty()){
                    txtDireccionEmpresa.setError("El Campo esta vacio");
                    txtDireccionEmpresa.requestFocus();
                    return;
                }

                    //Comprobacion de los RadioButton

                    if (RadioControl.isChecked() == true) {
                        TipoServicio = "Control";
                    } else if (RadioServicio.isChecked() == true) {
                        TipoServicio = "Servicio";
                    } if (RGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getActivity(),"Debe marcar un tipo de servicio",Toast.LENGTH_LONG).show();

                    RGroup.requestFocus();
                    return;
                }else {

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
        btnEnviar.setOnClickListener(new View.OnClickListener() {
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

        //Abriendo el documento y cargando datos
        document.open();
        document.addAuthor("AntiMouse");
        //document.add(new Paragraph(txtNombreEmpresa.getText().toString()));
        //document.add(new Paragraph(txtDireccionEmpresa.getText().toString()));

        document.addTitle("Orden de Trabajo");
        document.add(new Paragraph(TipoServicio));
        document.add(new Paragraph("Nombre Empresa: "+ txtNombreEmpresa));
        document.add(new Paragraph("Direccion Empresa: "+ txtDireccionEmpresa));

        /*
        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
        String htmlToPdf = "<html>" +
                "<head>" +
                "</head>" +
                "<body>" + "" + document.addTitle("Orden de Trabajo") +
                "<h2> Nombre Empresa: "+ txtNombreEmpresa.getText().toString() + "</h2>" +
                "</body> " +
                "<h1> Direccion Empresa: "+ txtDireccionEmpresa.getText().toString() + "</h1> " +
                "</html> ";

        String XHTML ="";

        worker.parseXHtml(pdfW, document, new StringReader(htmlToPdf));
        */
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
        email.putExtra(Intent.EXTRA_SUBJECT, txtNombreEmpresa.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, txtDireccionEmpresa.getText().toString());
        Uri uri = Uri.parse(myFile.getAbsolutePath());
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);
    }

    private void Limpiar(){
        txtNombreEmpresa.setText("");
        txtDireccionEmpresa.setText("");
        RadioControl.setChecked(false);
        RadioServicio.setChecked(false);
    }


    @Override
    public void onClick(View v) {
        return;
    }
}
