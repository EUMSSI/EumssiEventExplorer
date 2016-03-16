function getGraph(){
	var p_url="http://demo.eumssi.eu/Solr_EUMSSI/content_items/select?q=contentSearch%3Agermany&ident=true&facet=true&json.nl=map&facet.field=source&facet.field=meta.extracted.text_nerl.dbpedia.Country&facet.field=meta.extracted.text_nerl.dbpedia.City&facet.field=meta.extracted.text_nerl.dbpedia.PERSON&facet.field=meta.source.keywords&f.source.facet.limit=20&f.meta.extracted.text_nerl.dbpedia.Country.facet.limit=250&f.meta.extracted.text_nerl.dbpedia.City.facet.limit=25&f.meta.extracted.text_nerl.dbpedia.PERSON.facet.limit=50&f.meta.source.keywords.facet.limit=100&f.meta.extracted.text_nerl.dbpedia.Country.facet.mincount=1&f.meta.extracted.text_nerl.dbpedia.City.facet.mincount=1&f.meta.extracted.text_nerl.dbpedia.PERSON.facet.mincount=1&f.meta.source.keywords.facet.mincount=1&wt=json";
	$.ajax({
		url:p_url,
		success: function(result){
	        $("#my-genericgraph").html(result);
	    }});
}

