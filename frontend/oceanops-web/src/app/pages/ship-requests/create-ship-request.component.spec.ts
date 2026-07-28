import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateShipRequestComponent } from './create-ship-request.component';

describe('CreateShipRequestComponent', () => {
  let component: CreateShipRequestComponent;
  let fixture: ComponentFixture<CreateShipRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateShipRequestComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateShipRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
