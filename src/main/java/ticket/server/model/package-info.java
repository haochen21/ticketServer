@org.hibernate.annotations.GenericGenerator(
  name = "ID_GENERATOR",
  strategy = "enhanced-sequence",
  parameters = {
     @org.hibernate.annotations.Parameter(
        name = "sequence_name",
        value = "ticket_sequence"
     ),
     @org.hibernate.annotations.Parameter(
        name = "optimizer", 
        value = "pooled-lo"
     ),
     @org.hibernate.annotations.Parameter(
        name = "initial_value", 
        value = "1"
     ),
     @org.hibernate.annotations.Parameter(
        name = "increment_size", 
        value = "5"
     )
})
package ticket.server.model;