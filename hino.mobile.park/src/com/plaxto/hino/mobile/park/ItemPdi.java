package com.plaxto.hino.mobile.park;

import android.widget.EditText;

public class ItemPdi {
	  String nameChild;
	  String nameMaster;
	  String idChildPdi;
	  EditText textRemark;
	  String remark;
	  int image;
	  
	  boolean box;
	  

	  ItemPdi(String _nameChild, String _nameMaster, int _image, String _idChildPdi, boolean _box ) {
	    nameChild = _nameChild;
	    nameMaster = _nameMaster;
	    idChildPdi = _idChildPdi;
	    
	    image = _image;
	    box = _box;
	    
	  }
	}
